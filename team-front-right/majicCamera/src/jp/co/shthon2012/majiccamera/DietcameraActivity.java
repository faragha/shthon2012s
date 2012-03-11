package jp.co.shthon2012.majiccamera;

import jp.co.shthon2012.majiccamera.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DietcameraActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial);

        Button todayBtn = (Button)findViewById(R.id.todayBtn);
        Button historyBtn = (Button)findViewById(R.id.historyBtn);
        todayBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
    	// 戻るボタンが押されたとき
        if(e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // ボタンが離されたとき
            if (e.getAction() == KeyEvent.ACTION_UP) {
            	finish();
                return true;
            }
        }

       return super.dispatchKeyEvent(e);
    }
    public void onClick(View v) {
    	if(v.getId() == R.id.todayBtn){
    		Intent intent;
	        intent = new Intent(this, CameraPreviewActivity.class);
	        startActivity(intent);
    	}else{
	        Intent intent;
	        intent = new Intent(this, MagicImageGridActivity.class);
	        startActivity(intent);
    	}
    }

}