package jp.preety.ispants.oekaki;

import jp.preety.ispants.R;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.FrameLayout;

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
    AsyncHandler handler = AsyncHandler.createInstance();

    /**
     * 描画用のレイアウト
     */
    FrameLayout layout;

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
        (new ThreadSyncRunnerBase<Void>(handler) {
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
        glManager = glView.getGLManager();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!glManager.isInitialized()) {
                    glManager.initGL(handler);
                } else {
                    if (glView.isDestroyed()) {
                        glManager.onResume();
                    }
                }
            }
        });
    }

    /**
     * 描画を行う。
     * 呼び出しスレッドにかかわらず、必ずGLスレッドで描画が行われる。
     */
    public void rendering() {
    }
}
