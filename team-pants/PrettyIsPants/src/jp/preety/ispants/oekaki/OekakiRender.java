package jp.preety.ispants.oekaki;

import java.io.File;
import java.io.FileOutputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import jp.preety.ispants.R;
import jp.preety.ispants.oekaki.gesture.GestureController;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

import com.eaglesakura.lib.android.game.display.VirtualDisplay;
import com.eaglesakura.lib.android.game.graphics.Color;
import com.eaglesakura.lib.android.game.graphics.gl11.OpenGLManager;
import com.eaglesakura.lib.android.game.graphics.gl11.SpriteManager;
import com.eaglesakura.lib.android.game.thread.AsyncHandler;
import com.eaglesakura.lib.android.game.thread.ThreadSyncRunnerBase;
import com.eaglesakura.lib.android.game.thread.UIHandler;
import com.eaglesakura.lib.android.game.util.GameUtil;
import com.eaglesakura.lib.android.game.util.Holder;
import com.eaglesakura.lib.android.game.util.LogUtil;
import com.eaglesakura.lib.android.view.OpenGLView;

/**
 * 
 * @author TAKESHI YAMASHITA
 *
 */
public class OekakiRender implements Callback {

    /**
     * 
     */
    OekakiActivity activity;

    /**
     * 
     */
    OpenGLView glView = null;

    /**
     * 
     */
    OpenGLManager glManager;

    /**
     * 描画用のスプライトマネージャ
     */
    SpriteManager spriteManager;

    /**
     * GL用レンダラ
     */
    AsyncHandler renderHandler = AsyncHandler.createInstance();

    /**
     * 描画用のレイアウト
     */
    FrameLayout layout;

    /**
     * 描画用ドキュメント
     */
    Document document = new Document(this);

    /**
     * 
     */
    GestureController controller = new GestureController(this);

    /**
     * 
     * @param activity
     */
    public OekakiRender(OekakiActivity activity) {
        this.activity = activity;

        {
            layout = (FrameLayout) activity.findViewById(R.id.oekaki_area);
            glView = new OpenGLView(activity);
            glView.getHolder().addCallback(this);
            glView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    controller.onTouch(event);
                    return true;
                }
            });
            layout.addView(glView);
        }
    }

    /**
     * 
     */
    public void onPause() {
    }

    /**
     * 
     */
    public void onResume() {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 実行待ちを行う。
        (new ThreadSyncRunnerBase<Void>(renderHandler) {
            @Override
            public Void onOtherThreadRun() throws Exception {
                if (activity.isFinishing()) {
                    glManager.dispose();
                } else {
                    glManager.onPause();
                }
                return null;
            }
        }).run();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        final VirtualDisplay display = new VirtualDisplay();
        display.setRealDisplaySize(width, height);
        display.setVirtualDisplaySize(width, height);
        glManager = glView.getGLManager();
        renderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!glManager.isInitialized()) {

                    glManager.initGL(renderHandler);
                    glManager.updateDrawArea(display);
                    spriteManager = new SpriteManager(display, glManager);
                    initializeDatas();

                    rendering();
                } else {
                    if (glView.isDestroyed()) {
                        glManager.onResume();
                    }
                }
            }
        });
    }

    String waitImageGet() {
        String uri = null;
        final Holder<ProgressDialog> dialogHolder = new Holder<ProgressDialog>();
        Handler handler = new UIHandler();
        (new ThreadSyncRunnerBase<Void>(handler) {
            @Override
            public Void onOtherThreadRun() throws Exception {
                dialogHolder.set(new ProgressDialog(getActivity()));
                dialogHolder.get().setMessage("画像を受け取り中です");
                dialogHolder.get().setCanceledOnTouchOutside(false);
                dialogHolder.get().show();
                return null;
            }
        }).run();

        while (uri == null) {
            uri = activity.getIntent().getStringExtra(OekakiActivity.INTENT_IMAGE_RESP);
            GameUtil.sleep(1000);
        }

        (new ThreadSyncRunnerBase<Void>(handler) {
            @Override
            public Void onOtherThreadRun() throws Exception {
                dialogHolder.get().dismiss();
                return null;
            }
        }).run();

        return uri;
    }

    void initializeDatas() {
        try {
            String uri = activity.getIntent().getStringExtra(OekakiActivity.INTENT_IMAGE_URI);
            LogUtil.log("get uri :: " + uri);

            if (uri == null) {
                uri = waitImageGet();
            }

            document.loadBaseImage(activity, Uri.parse(uri), spriteManager.getVirtualDisplay());
            document.loadPenTextuers(activity);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * レンダリングを呼び出す。
     * 複数がqueueに溜まらないように、１つだけ登録する。
     */
    private Runnable renderRunnable = new Runnable() {
        @Override
        public void run() {
            rendering();
        }
    };

    public void capture(final File output) {
        if (!isRenderThread()) {
            throw new IllegalStateException("this is not render thread!!");
        }

        rendering();
        Rect imageArea = document.getBaseImageCorrector().getImageArea(new Rect());
        Bitmap data = getGLManager().captureSurfaceRGB888(imageArea);
        try {
            FileOutputStream os = new FileOutputStream(output);
            data.compress(CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            LogUtil.log(e);
        }
    }

    /**
     * 描画を行う。
     * 呼び出しスレッドにかかわらず、必ずGLスレッドで描画が行われる。
     */
    public void rendering() {
        if (!isRenderThread()) {
            // 登録済みのレンダリングタスクを削除し、新規タスクとして登録する。
            renderHandler.removeCallbacks(renderRunnable);
            renderHandler.post(renderRunnable);
            return;
        }

        glManager.clearColorRGBA(1, 1, 1, 1);
        glManager.clear();

        spriteManager.begin();
        {
            document.drawBaseImage(spriteManager);

            if (document.getBihakuLevel() > 0) {
                float alpha = (document.getBihakuLevel()) / 2;
                glManager.setBlendMode(OpenGLManager.BLEND_ALPHA_ADD);
                {
                    Rect area = document.getBaseImageCorrector().getImageArea(new Rect());
                    spriteManager.fillRect(area.left, area.top, area.width(), area.height(),
                            Color.toColorRGBA(255, 255, 255, (int) (alpha / 100 * 255)));
                }
                glManager.setBlendMode(OpenGLManager.BLEND_ALPHA_NORMAL);
            }
        }
        spriteManager.end();

        {
            GL11 gl = glManager.getGL();
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            {
                //! 確定済みのシェイプを描画する
                document.drawServerShapes(spriteManager);
            }

            //! ジェスチャ範囲を描画する
            controller.draw();
        }

        document.drawYohaku(spriteManager);

        glManager.swapBuffers();
    }

    /**
     * ドキュメントを取得する。
     * @return
     */
    public Document getDocument() {
        return document;
    }

    public SpriteManager getSpriteManager() {
        return spriteManager;
    }

    /**
     * 
     * @return
     */
    public OpenGLManager getGLManager() {
        return glManager;
    }

    /**
     * レンダリング用のハンドラを取得する。
     * @return
     */
    public AsyncHandler getRenderHandler() {
        return renderHandler;
    }

    /**
     * レンダリング用のスレッドで呼び出されている場合、trueを返す。
     * @return
     */
    public boolean isRenderThread() {
        return renderHandler.isHandlerThread();
    }

    public GestureController getController() {
        return controller;
    }

    public OekakiActivity getActivity() {
        return activity;
    }
}
