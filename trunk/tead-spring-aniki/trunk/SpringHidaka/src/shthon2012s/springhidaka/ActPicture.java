package shthon2012s.springhidaka;

import java.io.FileOutputStream;

import shthon2012s.springhidaka.Utils.Utils;
import shthon2012s.springhidaka.WebApis.UploadAsyncTask;
import shthon2012s.springhidaka.Worker.CreateImageAsyncTask;
import shthon2012s.springhidaka.Worker.CreateImageAsyncTaskCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class ActPicture extends Activity implements CreateImageAsyncTaskCallback {

	private Context ctx;
	Bitmap bm;

    public Handler h;
    private Runnable r;
    private static String mFilepath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);

		ctx = this.getApplicationContext();

		if(Intent.ACTION_VIEW.equals(getIntent().getAction())){

			Utils.createDrawbleCollection();
			String imageUrl = getIntent().getDataString();
			//データ生成
    		CreateImageAsyncTask get = new CreateImageAsyncTask(this, this);
    		get.execute( imageUrl );
		}else{

			// 画像のフルパスを取得するよ
			String imagePath = this.getIntent().getStringExtra("filepath");
			Overlay(imagePath);

			// 保存する場合はセーブフラグを用意
			Boolean isSave = this.getIntent().getBooleanExtra("saveflag", false);

			if (isSave) {
				saveImage(imagePath);
			}
		}

		mFilepath = new String();
	    h = new Handler();
	    r = new Runnable() {
	        @Override
	        public void run() {

	    		Overlay(mFilepath);
	    		saveImage(mFilepath);
	        }
	    };


		//どっかきめたフォルダに保存したの読んでgridviewとか？
		//ファイル名を時刻＋セリフにしちゃえば管理楽なんじゃね
	}

	private void saveImage(String imagePath) {
		try {
			FileOutputStream out = openFileOutput(imagePath,
					Context.MODE_WORLD_READABLE);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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




	@Override
	public void onSuccessCreateImage(String filepath) {

		mFilepath = filepath;
		h.post(r);
	}

	@Override
	public void onFailedCreateImage(int statusCode) {

		Toast.makeText(getApplicationContext(), "ごめんね。失敗しちゃった…" + Integer.toString(statusCode), Toast.LENGTH_SHORT).show();
	}
}
