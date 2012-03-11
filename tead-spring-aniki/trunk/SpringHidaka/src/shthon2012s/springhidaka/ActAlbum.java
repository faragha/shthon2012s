package shthon2012s.springhidaka;

import shthon2012s.springhidaka.Utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.ImageView;

public class ActAlbum extends Activity {

	private Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album);

		//ランダムに生成する
		Utils.createDrawbleCollection();

		ctx = this.getApplicationContext();

		// ためしにかさねてみたよ
		testOverlay();

		//どっかきめたフォルダに保存したの読んでgridviewとか？
		//ファイル名を時刻＋セリフにしちゃえば管理楽なんじゃね
	}

	private void testOverlay() {

		Bitmap bm;
		Bitmap bmo;
		Canvas mCanvas;

		int resId = Utils.getDrawableFrameId();
		bm = BitmapFactory.decodeResource(getResources(), resId);
		bm = bm.copy(Bitmap.Config.ARGB_8888, true);
		mCanvas = new Canvas(bm);

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
