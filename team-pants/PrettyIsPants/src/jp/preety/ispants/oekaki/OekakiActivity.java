package jp.preety.ispants.oekaki;

import jp.preety.ispants.R;
import jp.preety.ispants.bluetooth.BluetoothActivity;
import android.os.Bundle;

import com.eaglesakura.lib.android.game.util.LogUtil;

/**
 * お絵かき用のActivity
 * @author TAKESHI YAMASHITA
 *
 */
public class OekakiActivity extends BluetoothActivity {

    /**
     * 画像の保存先を伝える
     */
    public static final String INTENT_IMAGE_URI = "INTENT_IMAGE_URI";

    /**
     * お絵かき用のレンダリングオブジェクト
     */
    OekakiRender render = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    /**
     * 他の端末からデータを受け取った
     */
    @Override
    protected void onMessageRead(String message) {
        render.getDocument().getServer().add(message);
    }
}
