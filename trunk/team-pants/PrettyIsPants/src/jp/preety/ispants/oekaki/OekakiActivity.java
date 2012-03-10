package jp.preety.ispants.oekaki;

import android.app.Activity;
import android.os.Bundle;

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

    OekakiRender render = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
