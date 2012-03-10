package jp.sonicstudio.sutami.activity;

import jp.sonicstudio.sutami.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SutamiActivity extends Activity {

	private static final int REQUEST_GET_CONTENT = 0;
	private static final String TAG = "SutamiActivity";
	private final static int REQUEST_GET_CAMERA_IMAGE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button buttonTakeAPicture = (Button) findViewById(R.id.button_taka_a_picture);
		buttonTakeAPicture.setOnClickListener(mTakeAPictureOnClickListener);
		Button buttonPickAPictureFromGalrally = (Button) findViewById(R.id.button_pick_a_picture);
		buttonPickAPictureFromGalrally
				.setOnClickListener(mPickAPictureFromGarallyOnClickListener);
	}

	private Uri mCameraImageUri;
	private View.OnClickListener mTakeAPictureOnClickListener = new View.OnClickListener() {
	    
		@Override
		public void onClick(View v) {
		   
        	    ContentValues values = new ContentValues();
        
        	    String filename = System.currentTimeMillis() + ".jpg";
        	    values.put(MediaStore.Images.Media.TITLE, filename);
        	    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
 
        	    // 保存先
        	    mCameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        
        	    Intent intent = new Intent();
        	    // インテントにアクションをセット
        	    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        	    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
        	    // カメラアプリ起動
        	    startActivityForResult(intent, REQUEST_GET_CAMERA_IMAGE);
		}
	};

	private View.OnClickListener mPickAPictureFromGarallyOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// ギャラリーから画像を取得する
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_GET_CONTENT);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean handeled = false;
		if (resultCode == RESULT_OK)
			switch (requestCode) {
			case REQUEST_GET_CONTENT:
				try {
					Uri uri = data.getData();
					Intent intent = new Intent(this, PreviewActivity.class);
					intent.putExtra(PreviewActivity.IMAGE_URI, uri);
					startActivity(intent);
					handeled = true;
				} catch (Exception e) {
				}
				break;
			case REQUEST_GET_CAMERA_IMAGE:
			    
                		Uri uri = null;
                		if (data != null) {
                		    uri = data.getData();
                		}
				
				// URIを取得できない場合、Intent生成時のURIを参照先に設定
				if(uri == null){
				    uri = mCameraImageUri;
				}
				
				Intent intent = new Intent(this, PreviewActivity.class);
				intent.putExtra(PreviewActivity.IMAGE_URI, uri);
				startActivity(intent);
				handeled = true;
				break;
			}
		if (!handeled) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}