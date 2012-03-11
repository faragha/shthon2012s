package shthon2012s.springhidaka.WebApis;

import shthon2012s.springhidaka.R;
import shthon2012s.springhidaka.R.id;
import shthon2012s.springhidaka.R.layout;
import shthon2012s.springhidaka.Worker.CreateImageAsyncTask;
import shthon2012s.springhidaka.Worker.CreateImageAsyncTaskCallback;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AsyncTaskSample extends Activity implements UploadAsyncTaskCallback, OnClickListener, CreateImageAsyncTaskCallback{


	private String imageUrl;
	private EditText edit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webapisample);

        Button btPost = (Button)findViewById(R.id.bt_post);
        Button btGet = (Button)findViewById(R.id.bt_get);
		edit = (EditText)findViewById(R.id.editText1);

		btPost.setOnClickListener(this);
		btGet.setOnClickListener(this);

    }

	public void onClick(View v) {

        switch (v.getId()) {

        case R.id.bt_post:
    		UploadAsyncTask post = new UploadAsyncTask(this, this);
    		post.execute( "/sdcard/sample500_500.jpg" );
    		break;

        case R.id.bt_get:
    		CreateImageAsyncTask get = new CreateImageAsyncTask(this, this);
    		get.execute( imageUrl );
    		break;
    	default:
    		break;
        }

	}

	@Override
	public void onSuccessUploadImage(String url) {
		Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
		imageUrl = url;
		edit.setText(imageUrl);

	}

	@Override
	public void onFailedUploadImage(int statusCode) {
		Toast.makeText(getApplicationContext(), "Failed, ResponseCode:" + Integer.toString(statusCode), Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onSuccessCreateImage(String filepath) {
		Toast.makeText(getApplicationContext(), filepath, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFailedCreateImage(int statusCode) {
		Toast.makeText(getApplicationContext(), "Failed, ResponseCode:" + Integer.toString(statusCode), Toast.LENGTH_SHORT).show();
	}
}