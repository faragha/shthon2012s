package shthon2012s.springhidaka.WebApis;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AsyncTaskSample extends Activity implements UploadAsyncTaskCallback, OnClickListener{


/*
	private EditText edit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button btn = (Button)findViewById(R.id.button1);
		edit = (EditText)findViewById(R.id.editText1);

		btn.setOnClickListener(this);

    }
*/
	public void onClick(View v) {

        //SpannableStringBuilder sb = (SpannableStringBuilder)edit.getText();
        //String str = sb.toString();

		UploadAsyncTask post = new UploadAsyncTask(this, this);
		post.execute( "/sdcard/sample500_500.jpg" );
	}

	@Override
	public void onSuccessUploadImage(String url) {
		Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onFailedUploadImage(int resId) {
		Toast.makeText(getApplicationContext(), "Failed, ResponseCode:" + Integer.toString(resId), Toast.LENGTH_SHORT).show();

	}
}