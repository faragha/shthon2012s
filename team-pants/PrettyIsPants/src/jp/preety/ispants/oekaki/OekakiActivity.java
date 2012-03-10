package jp.preety.ispants.oekaki;

import java.io.File;

import jp.preety.ispants.R;
import jp.preety.ispants.bluetooth.BluetoothActivity;
import jp.preety.ispants.oekaki.data.Pen;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

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

    public void startCapture() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("画像を保存中です");
        dialog.show();
        render.getRenderHandler().post(new Runnable() {

            @Override
            public void run() {
                render.capture(new File(Environment.getExternalStorageDirectory(), "output.jpg"));
                dialog.dismiss();
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
        render.getDocument().getServer().add(message);
    }

    @Override
    protected void sendToOtherDevice(String sendData) {
        super.sendToOtherDevice(sendData);
    }
}
