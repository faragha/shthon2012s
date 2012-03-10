package jp.preety.ispants.oekaki;

import android.widget.FrameLayout;

import com.eaglesakura.lib.android.game.graphics.gl11.OpenGLManager;
import com.eaglesakura.lib.android.game.graphics.gl11.SpriteManager;
import com.eaglesakura.lib.android.game.thread.AsyncHandler;
import com.eaglesakura.lib.android.view.OpenGLView;

/**
 * 
 * @author TAKESHI YAMASHITA
 *
 */
public class OekakiRender {
    OekakiActivity activity;
    OpenGLView glView = null;
    OpenGLManager glManager;
    SpriteManager spriteManager;
    AsyncHandler handler;

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

    /**
     * 描画を行う。
     * 呼び出しスレッドにかかわらず、必ずGLスレッドで描画が行われる。
     */
    public void rendering() {
    }
}
