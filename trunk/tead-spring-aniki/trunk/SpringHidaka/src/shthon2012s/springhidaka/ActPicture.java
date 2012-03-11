package shthon2012s.springhidaka;

import java.io.FileOutputStream;

import shthon2012s.springhidaka.Utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ActPicture extends Activity {

	private Context ctx;
	Bitmap bm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);

		ctx = this.getApplicationContext();

		// ためしにかさねてみたよ
		String imagePath = this.getIntent().getStringExtra("filepath");
		Overlay(imagePath);

		Boolean isSave = this.getIntent().getBooleanExtra("saveflag", false);

		if (isSave) {
			try {
				FileOutputStream out = openFileOutput(imagePath,
						Context.MODE_WORLD_READABLE);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//どっかきめたフォルダに保存したの読んでgridviewとか？
		//ファイル名を時刻＋セリフにしちゃえば管理楽なんじゃね
	}

	private void Overlay(String imagePath) {

		Bitmap bmo;
		Bitmap photo;

		int resId;

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.outHeight = 500;
		opt.outWidth = 500;
		opt.inJustDecodeBounds = false;

		resId = Utils.getDrawableFrameId();
		bm =  BitmapFactory.decodeResource(getResources(), resId);
		bm = bm.copy(Bitmap.Config.ARGB_8888, true);

		Canvas mCanvas = new Canvas(bm);

		bmo = BitmapFactory.decodeFile(imagePath, opt);
		mCanvas.drawBitmap(bmo, 0, 0, null);

		resId = Utils.getDrawableFrameId();
		bmo =  BitmapFactory.decodeResource(getResources(), resId);
		mCanvas.drawBitmap(bmo, 0, 0, null);

		resId = Utils.getDrawableCopy1Id();
		bmo =  BitmapFactory.decodeResource(getResources(), resId);
		mCanvas.drawBitmap(bmo, 0, 0, null);

		resId = Utils.getDrawableCopy2Id();
		bmo =  BitmapFactory.decodeResource(getResources(), resId);
		mCanvas.drawBitmap(bmo, 0, 0, null);

		resId = Utils.getDrawableCopy3Id();
		bmo =  BitmapFactory.decodeResource(getResources(), resId);
					mCanvas.drawBitmap(bmo, 0, 0, null);

		ImageView image = (ImageView) findViewById(R.id.imageView1);
		image.setImageBitmap(bm);

	}
}
