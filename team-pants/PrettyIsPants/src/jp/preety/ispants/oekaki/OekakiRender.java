package jp.preety.ispants.oekaki;

import jp.preety.ispants.R;
import jp.preety.ispants.oekaki.gesture.GestureController;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

import com.eaglesakura.lib.android.game.display.VirtualDisplay;
import com.eaglesakura.lib.android.game.graphics.gl11.OpenGLManager;
import com.eaglesakura.lib.android.game.graphics.gl11.SpriteManager;
import com.eaglesakura.lib.android.game.thread.AsyncHandler;
import com.eaglesakura.lib.android.game.thread.ThreadSyncRunnerBase;
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

    void initializeDatas() {
        try {
            String uri = activity.getIntent().getStringExtra(OekakiActivity.INTENT_IMAGE_URI);
            uri = "file:///sdcard/sample.jpg";

            document.loadBaseImage(activity, Uri.parse(uri), spriteManager.getVirtualDisplay());
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
        }
        spriteManager.end();

        {
            //! ジェスチャ範囲を描画する
            controller.draw();
        }

        glManager.swapBuffers();
    }

    /**
     * ドキュメントを取得する。
     * @return
     */
    public Document getDocument() {
        return document;
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
}
