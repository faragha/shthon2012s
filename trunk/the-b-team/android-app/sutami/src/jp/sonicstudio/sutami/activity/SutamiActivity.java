package jp.sonicstudio.sutami.activity;

import jp.sonicstudio.sutami.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SutamiActivity extends Activity {

	private static final int REQUEST_GET_CONTENT = 0;
	private static final String TAG = "SutamiActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button buttonTakeAPicture = (Button) findViewById(R.id.button_pick_a_picture);
		buttonTakeAPicture.setOnClickListener(mTakeAPictureOnClickListener);
		Button buttonPickAPictureFromGalrally = (Button) findViewById(R.id.button_pick_a_picture);
		buttonPickAPictureFromGalrally
				.setOnClickListener(mPickAPictureFromGarallyOnClickListener);
	}

	private View.OnClickListener mTakeAPictureOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

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
			}
		if (!handeled) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}