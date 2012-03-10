package jp.preety.ispants.oekaki;

import jp.preety.ispants.R;
import android.app.Activity;
import android.os.Bundle;

import com.eaglesakura.lib.android.game.util.LogUtil;

/**
 * お絵かき用のActivity
 * @author TAKESHI YAMASHITA
 *
 */
public class OekakiActivity extends Activity {

    /**
     * 画像の保存先を伝える
     */
    public static final String INTENT_IMAGE_URI = "INTENT_IMAGE_URI";

    /**
     * お絵かき用のレンダリングオブジェクト
     */
    OekakiRender render = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oekaki);

        LogUtil.setOutput(true);
        render = new OekakiRender(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        render.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        render.onResume();
    }
}
