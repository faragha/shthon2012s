
package shthon2012s.springhidaka;

import java.io.File;
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
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ActPicture extends Activity implements CreateImageAsyncTaskCallback {

    private Context ctx;

    public Handler mHandler;

    private Runnable r;

    private static String mFilepath;

    private ImageView mImage;

    private Bitmap bmo;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);

		ctx = this.getApplicationContext();

		//毎回やっておく
		Utils.createDrawbleCollection();

		if(Intent.ACTION_VIEW.equals(getIntent().getAction())){
		    //暗黙的インテントで呼び出されたとき（インテントフィルタを想定）
			String imageUrl = getIntent().getDataString();
			//データ生成
    		CreateImageAsyncTask get = new CreateImageAsyncTask(this, this);
    		get.execute( imageUrl );

            Button bt_show = (Button) findViewById(R.id.bt_show);
            bt_show.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    GoAlbum();
                    finish();
                }
            });

		}else{
			// 画像のフルパスを取得するよ
			String imagePath = this.getIntent().getStringExtra("filepath");
			Display(imagePath);

            Button bt_show = (Button) findViewById(R.id.bt_show);
            bt_show.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });

		}

		mFilepath = new String();

	    mHandler = new Handler();
	    r = new Runnable() {
	        @Override
	        public void run() {

	    		Overlay(mFilepath);
	    		saveImage(mFilepath);
	        }
	    };
	}

    protected void GoAlbum() {
        Intent it = new Intent(ctx, ActAlbum.class);
        startActivity(it);

    }

    private void saveImage(String imagePath) {
        try {

            File file = new File(imagePath);
            FileOutputStream out = new FileOutputStream(file);

            BitmapDrawable bd = (BitmapDrawable) mImage.getDrawable();
            Bitmap bm = bd.getBitmap();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            Toast.makeText(ctx, "アルバムに保存しました", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Overlay(String imagePath) {

        Bitmap bmo;
        int resId;

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.outHeight = 500;
        opt.outWidth = 500;
        opt.inJustDecodeBounds = false;

        resId = Utils.getDrawableFrameId();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
        bm = bm.copy(Bitmap.Config.ARGB_8888, true);

        Canvas mCanvas = new Canvas(bm);

        bmo = BitmapFactory.decodeFile(imagePath, opt);
        mCanvas.drawBitmap(bmo, 0, 0, null);

        resId = Utils.getDrawableFrameId();
        bmo = BitmapFactory.decodeResource(getResources(), resId);
        mCanvas.drawBitmap(bmo, 0, 0, null);

        resId = Utils.getDrawableCopy1Id();
        bmo = BitmapFactory.decodeResource(getResources(), resId);
        mCanvas.drawBitmap(bmo, 0, 0, null);

        resId = Utils.getDrawableCopy2Id();
        bmo = BitmapFactory.decodeResource(getResources(), resId);
        mCanvas.drawBitmap(bmo, 0, 0, null);

        resId = Utils.getDrawableCopy3Id();
        bmo = BitmapFactory.decodeResource(getResources(), resId);
        mCanvas.drawBitmap(bmo, 0, 0, null);

        mImage = (ImageView) findViewById(R.id.imageView1);
        mImage.setImageBitmap(bm);

    }

    private void Display(String imagePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.outHeight = 500;
        opt.outWidth = 500;
        opt.inJustDecodeBounds = false;

        bmo = BitmapFactory.decodeFile(imagePath, opt);

        mImage = (ImageView) findViewById(R.id.imageView1);
        mImage.setImageBitmap(bmo);

    }

    @Override
    public void onSuccessCreateImage(String filepath) {

        mFilepath = filepath;
        mHandler.post(r);
    }

    @Override
    public void onFailedCreateImage(int statusCode) {

        Toast.makeText(getApplicationContext(), "ごめんね。失敗しちゃった…" + Integer.toString(statusCode),
                Toast.LENGTH_SHORT).show();
    }
}
