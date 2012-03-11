package jp.co.shthon2012.ikemen_hunter3;

import jp.co.shthon2012.ikemen_counterattack.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Ikemen_hunterActivity extends Activity implements OnClickListener {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);

        ImageView imgView = (ImageView)findViewById(R.id.titleImg);
        imgView.setOnClickListener(this);

        // 無操作で暗くなるのを防ぐ
        Window window = getWindow();
        window.addFlags(
        	WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void onClick(View v) {
    	finish();

        Intent intent;
        intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}