package shthon2012s.springhidaka;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ActAlbum extends Activity {

	private Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album);

		ctx = this.getApplicationContext();

		// ためしにかさねてみたよ
		testOverlay();

		//どっかきめたフォルダに保存したの読んでgridviewとか？
		//ファイル名を時刻＋セリフにしちゃえば管理楽なんじゃね
	}

	private void testOverlay() {

		InputStream is;
		Bitmap bm;
		Bitmap bmo;
		Canvas mCanvas;
		try {
			is = getResources().getAssets().open("frame_01.png");
			bm = BitmapFactory.decodeStream(is);
			bm = bm.copy(Bitmap.Config.ARGB_8888, true);
			mCanvas = new Canvas(bm);

			is = getResources().getAssets().open("copy1_01.png");
			bmo = BitmapFactory.decodeStream(is);
			mCanvas.drawBitmap(bmo, 0, 0, null);
			is = getResources().getAssets().open("copy2_01.png");
			bmo = BitmapFactory.decodeStream(is);
			mCanvas.drawBitmap(bmo, 0, 0, null);
			is = getResources().getAssets().open("copy3_01.png");
			bmo = BitmapFactory.decodeStream(is);
			mCanvas.drawBitmap(bmo, 0, 0, null);

			ImageView image = (ImageView) findViewById(R.id.imageView1);
			image.setImageBitmap(bm);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
