package jp.preety.ispants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import jp.preety.ispants.oekaki.OekakiActivity;

/**
 * Confirm Activity
 * @author pretty is pants
 */
public class ConfirmActivity extends Activity implements View.OnClickListener {
    /**
     * 画像の保存先を伝える
     */
    public static final String INTENT_IMAGE_URI = "INTENT_IMAGE_URI";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm);
        
        // 画像設定
        final Uri uri = Uri.parse(getImageUriString());
        final ImageView imgView = (ImageView)findViewById(R.id.img_photo);
        imgView.setImageURI(uri);
        
        // ボタンイベント登録
        final Button btnOk = (Button)findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    
    /**
     * Event Hanlder: on click view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok: {
                final Intent intent = new Intent(this, OekakiActivity.class);
                intent.putExtra(OekakiActivity.INTENT_IMAGE_URI, getImageUriString());
                startActivity(intent);
            }
            case R.id.btn_cancel: {
                finish();
                break;
            }
        }
    }
    
    /**
     * ラクガキ対象の画像URIを取得
     */
    private String getImageUriString() {
        return getIntent().getStringExtra(INTENT_IMAGE_URI);
    }
}