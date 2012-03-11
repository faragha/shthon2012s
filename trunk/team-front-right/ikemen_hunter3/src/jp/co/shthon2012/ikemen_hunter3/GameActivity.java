package jp.co.shthon2012.ikemen_hunter3;

import jp.co.shthon2012.ikemen_counterattack.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class GameActivity extends Activity implements OnClickListener{
	MediaPlayer mMediaPlayer;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View gameView = (View)findViewById(R.id.gameView);
        gameView.setOnClickListener(this);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
    	// 戻るボタンが押されたとき
        if(e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // ボタンが離されたとき
            if (e.getAction() == KeyEvent.ACTION_UP) {
            	MainSurfaceView.stopBGM();
            }
        }

       return super.dispatchKeyEvent(e);
    }

    public void onClick(View v) {
    	if(!MainSurfaceView.getGameOverFlg()){
    		return;
    	}
        setContentView(R.layout.main);

        View gameView = (View)findViewById(R.id.gameView);
        gameView.setOnClickListener(this);
    }
}
