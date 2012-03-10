package jp.sonicstudio.sutami.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jp.sonicstudio.sutami.R;
import jp.sonicstudio.sutami.image.PreviewView;
import jp.sonicstudio.sutami.image.StampMaker;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

public class PreviewActivity extends Activity {

	private static final String TAG = "PreviewActivity";
	public static final String IMAGE_URI = "image_uri";

	private static final int MAX_WIDTH = 320;
	private static final int MAX_HEIGHT = 320;

	private PreviewView mPreviewView;
	private Bitmap mSrcBitmap;
	private Bitmap mDstBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		mPreviewView = (PreviewView) findViewById(R.id.previewview);
		Intent intent = getIntent();
		Uri uri = (Uri) intent.getParcelableExtra(IMAGE_URI);
		// Log.v(TAG, uri.toString());
		initializeView(uri);
	}

	private void initializeView(Uri uri) {
		// 画像のサイズを取得
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		InputStream inputStream;
		try {
			inputStream = getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(inputStream, null, options);
			inputStream.close();
			int scaleWidth = 1;
			int scaleHeight = 1;
			if ((options.outWidth > MAX_WIDTH)
					|| (options.outHeight > MAX_HEIGHT)) {
				scaleWidth = options.outWidth / MAX_WIDTH + 1;
				scaleHeight = options.outHeight / MAX_HEIGHT + 1;
			}
			int scale = Math.max(scaleWidth, scaleHeight);
			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;
			inputStream = getContentResolver().openInputStream(uri);
			mSrcBitmap = BitmapFactory.decodeStream(inputStream, null, options);
			inputStream.close();
			StampMaker stampMaker = new StampMaker();
			stampMaker.initialize(mSrcBitmap);
			mDstBitmap = stampMaker.process(128);
			mPreviewView.setBitmap(mDstBitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
