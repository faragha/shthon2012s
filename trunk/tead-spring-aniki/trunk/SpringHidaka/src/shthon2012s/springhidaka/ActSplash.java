package shthon2012s.springhidaka;

import shthon2012s.springhidaka.WebApis.AsyncTaskSample;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
public class ActSplash extends Activity {

	private Button bt_camera;
	private Button bt_show;
	private ImageView iv_demo;

	private Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        bt_camera = (Button) findViewById(R.id.bt_camera);
        bt_show = (Button) findViewById(R.id.bt_show);
        iv_demo = (ImageView) findViewById(R.id.iv_dummybtn);


        iv_demo.setAlpha(30);
        iv_demo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DemoPlay();
			}
		});

        //iv_demo.setVisibility(8);

        ctx=this.getApplicationContext();
        bt_camera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GoCamera();
			}
		});
        bt_show.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GoAlbum();
			}
		});
    }


	protected void DemoPlay() {

		Intent it = new Intent(ctx, AsyncTaskSample.class);
		startActivity(it);
	}


	protected void GoAlbum() {
		Intent it = new Intent(ctx, ActAlbum.class);
		startActivity(it);

	}


	protected void GoCamera() {

		Intent it = new Intent(ctx, ActCamera.class);
		startActivity(it);
	}
}