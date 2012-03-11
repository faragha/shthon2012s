package jp.preety.ispants.oekaki;

import java.io.File;

import jp.preety.ispants.R;
import jp.preety.ispants.bluetooth.BluetoothActivity;
import jp.preety.ispants.oekaki.data.Data;
import jp.preety.ispants.oekaki.data.Pen;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.eaglesakura.lib.android.game.thread.UIHandler;
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
     * bluetoothで受け取った画像のURI。
     * こっちに格納される場合がある。
     */
    public static final String INTENT_IMAGE_RESP = "INTENT_IMAGE_RESP";

    /**
     * キャッシュファイルの保存先
     */
    private static final File CACHE_FILE = new File(Environment.getExternalStorageDirectory(), ".pahts.cache");

    /**
     * お絵かき用のレンダリングオブジェクト
     */
    OekakiRender render = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oekaki);
        // 仮でリスな登録
        {
            findViewById(R.id.oekaki_pen_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pen pen = new Pen();
                    pen.setTegakiData(5, 128, 128, 0);
                    render.getDocument().setPen(pen);
                }
            });

            findViewById(R.id.oekaki_stamp_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pen pen = new Pen();
                    pen.setStampData("");
                    render.getDocument().setPen(pen);
                }
            });

            findViewById(R.id.oekaki_finish_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new Builder(OekakiActivity.this);
                    builder.setMessage("画像を保存して終了しますか？").setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startCapture();
                        }
                    }).show();
                }
            });
        }
        LogUtil.setOutput(true);
        render = new OekakiRender(this);
    }

    /**
     * 画像のキャプチャを行う。
     * 処理自体は非同期で裏で行われるから、終わったらdismissの必要がある。
     */
    public void startCapture() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("画像を保存中です");
        dialog.show();
        render.getRenderHandler().post(new Runnable() {
            @Override
            public void run() {
                render.capture(new File(Environment.getExternalStorageDirectory(), "output.png"));
                (new UIHandler()).post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });
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
        Data data = Data.fromRececiveData(message);

        if (data.image == null) {
            render.getDocument().getServer().add(data, message);
        } else {

        }
    }

    @Override
    protected void sendToOtherDevice(String sendData) {
        super.sendToOtherDevice(sendData);
    }
}
