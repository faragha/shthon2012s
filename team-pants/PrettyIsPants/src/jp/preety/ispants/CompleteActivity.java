package jp.preety.ispants;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Complete Activity
 * @author pretty is pants
 */
public class CompleteActivity extends Activity implements View.OnClickListener {
    /**
     * 画像の保存先を伝える
     */
    public static final String INTENT_IMAGE_URI = "INTENT_IMAGE_URI";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete);
        
        // ボタンイベント登録
        final Button btnOk = (Button)findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
    }
    
    /**
     * Event Hanlder: on click view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok: {
                finish();
                break;
            }
        }
    }
}