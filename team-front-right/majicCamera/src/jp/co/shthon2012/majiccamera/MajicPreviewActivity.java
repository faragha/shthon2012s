package jp.co.shthon2012.majiccamera;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.shthon2012.majiccamera.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MajicPreviewActivity extends Activity implements OnTouchListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLayout();
    }

	private ImageView majicImg;
	private float oriScaleX;
	private float nowScaleX;
	private float nowScaleY;
	private final float SCALE_X_MAGNIFY = (float)1.3;

    private void createLayout(){
    	setContentView(R.layout.main);
    	majicImg = (ImageView)findViewById(R.id.majicImg);

        Intent intent = getIntent();
        String str = "";
        if(intent != null){
            str = intent.getStringExtra("imgPath");
            if(str == null){
	        	//最新の写真を表示

	            // SDカードのFileを取得
	            File file = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
	            List<String> dirList = new ArrayList<String>();
	            dirList.add(file.getPath());

	            // SDカード内のファイルを検索。
	            int m = 0;
	            int n = 0;
	            long lastModifyTime = 0;

	            while(dirList.size() > m){
	                File subDir = new File(dirList.get(m));
	                String subFileName[] = subDir.list();
	                n = 0;
	                while(subFileName.length > n){
	                    File subFile = new File(subDir.getPath() + "/" + subFileName[n]);
	                    if(subFile.getName().endsWith("jpg") || subFile.getName().endsWith("JPG")){
	                    	String imgFilePath =subDir.getPath() + "/" + subFileName[n];
	                    	File tmpFile = new File(imgFilePath);
	                    	long modifytime = tmpFile.lastModified();
	                    	if(lastModifyTime < modifytime){
	                    		lastModifyTime = modifytime;
	                    		str = imgFilePath;
	                    	}
	                    }
	                    n++;
	                }
	                m++;
	            }
            }
        }

        //画像読み込み
		Bitmap _bmpImageFile = BitmapFactory.decodeFile(str);

    	majicImg.setImageBitmap(_bmpImageFile);
    	RelativeLayout.LayoutParams settingImgLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	settingImgLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
    	majicImg.setLayoutParams(settingImgLayout);

		oriScaleX = SCALE_X_MAGNIFY;
		nowScaleX = oriScaleX;
		nowScaleY = 1;

    	Matrix matrix = new Matrix();
    	float startx = ((640 * nowScaleX) - 640)/2;
    	matrix.setScale(nowScaleX, 1, startx,0);
    	majicImg.setImageMatrix(matrix);
    	majicImg.setOnTouchListener(this);

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
    	// 戻るボタンが押されたとき
        if(e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // ボタンが離されたとき
            if (e.getAction() == KeyEvent.ACTION_UP) {
            	finish();
            	Intent intent;
                intent = new Intent(this, MagicImageGridActivity.class);
                startActivity(intent);
                return true;
            }
        }

       return super.dispatchKeyEvent(e);
    }

    private final int MOVE_DISTANCE = 1200;
    private int preX = -1;
    private int preY = -1;
    private int distance=0;
    private boolean isNormal = false;
    private final float FINAL_SCALE = (float)0.7;

    @Override
	public boolean onTouch(View v, MotionEvent event) {
		int tx = (int) event.getRawX();
		int ty = (int) event.getRawY();

		//1回目のムーブでまず動いてしまうのを避ける
		//TODO タッチアップでpreXを-1に戻す
		if(preX==-1){
			preX = tx;
			preY = ty;
			return true;
		}

		if(nowScaleX <= FINAL_SCALE){
			return true;
		}

		int x = tx - preX;
		int y = ty - preY;

		preX = tx;
		preY = ty;

		//移動距離を求める
		distance += Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

		if(distance > MOVE_DISTANCE){
			distance = 0;
			nowScaleX -= 0.05;

	    	Matrix matrix = new Matrix();
	    	float startx = ((640 * nowScaleX) - 640)/2;
	    	if(nowScaleX < 1.0){
	    		startx = 0;
	    	}
	    	matrix.setScale(nowScaleX, nowScaleY, startx,0);
	    	majicImg.setImageMatrix(matrix);

	    	if(nowScaleX <= 1.0 && !isNormal ){
	    		Toast.makeText(this, "本当の自分に戻ったよ！", Toast.LENGTH_LONG).show();
	    		isNormal = true;
	    	}

	    	if(nowScaleX <= FINAL_SCALE ){
	    		Toast.makeText(this, "理想の自分になったよ！", Toast.LENGTH_LONG).show();
	    	}
		}

		return true;
    }
}
