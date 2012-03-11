package jp.shigepon.ikemenhunter;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IkemenHunterActivity extends Activity implements OnTouchListener {

	private LinearLayout layout;
	private TextView msg;
	
	private Random r;
	private Resources rs;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
    	r = new Random(Calendar.getInstance().getTimeInMillis());
    	layout = (LinearLayout)findViewById(R.id.view);
    	layout.setOnTouchListener(this);
    	msg = (TextView)findViewById(R.id.msg);
    	rs = getResources();
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	return true;
    }

    @Override
    public void onResume() {
    	super.onResume();
    	layout.setBackgroundResource(R.drawable.ikemen1 + r.nextInt(3));
    	Drawable d = rs.getDrawable(R.drawable.bg);
    	d.setAlpha(128);
    	msg.setBackgroundDrawable(d);
    	String str = rs.getString(R.string.msg0 + r.nextInt(68));
    	int div = ((str.length() - 1) / 10) + 1;
    	float dip = (float) (320.0 * div / str.length());
    	if (dip > 48) {
    		dip = 48;
    	}
    	msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dip);
    	msg.setText(str);
        startService(new Intent(this, IkemenHunterService.class));
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	finish();
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		finish();
		return true;
	}
}
