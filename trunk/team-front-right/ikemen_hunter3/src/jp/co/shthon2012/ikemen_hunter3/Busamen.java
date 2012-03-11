package jp.co.shthon2012.ikemen_hunter3;

import java.util.Random;

import jp.co.shthon2012.ikemen_counterattack.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Busamen {
	Context mContext;

	// ブサメン画像
	private Drawable mDrawable;
	private int mWidth;
	private int mHeight;
	private Rect mRect;
    int mBusamenImg[] = {R.drawable.a01, R.drawable.a02, R.drawable.a03, R.drawable.a04, R.drawable.a05, R.drawable.a06,
    		R.drawable.a07, R.drawable.a08, R.drawable.a09, R.drawable.a10, R.drawable.a11, R.drawable.a12,
    		R.drawable.a13, R.drawable.a14, R.drawable.a15, R.drawable.a16, R.drawable.a17, R.drawable.a18, R.drawable.a19};

	// 表示範囲
	private Rect mViewRect;

	// 移動量
	private double mDy = 3;

	public Busamen(Context context, Rect viewRect) {
		mContext = context;

		// 画像を設定
		Drawable drawable = getImg();
		mDrawable = drawable;
	    mWidth = drawable.getIntrinsicWidth();
	    mHeight = drawable.getIntrinsicHeight();
	    mRect = new Rect(0, 0, mWidth, mHeight);

	    // 表示範囲を設定
	    mViewRect = viewRect;

	    //X軸の位置を指定
	    Random rnd = new Random();
        int ran = rnd.nextInt(mViewRect.right - mWidth*2);
        ran += mWidth;

	    // 初期位置を設定
	    mRect.offset(ran, 0);

	    mDy = getSpeed();
	}

	public void move() {
		// ブサメンを移動
	    if(mRect.bottom >= mViewRect.bottom){
	    	restartBusamen();
	    }else{
	    	mRect.offset(0, (int)mDy);
	    }
	}

	public void restartBusamen(){
	    //X軸の位置を指定
	    Random rnd = new Random();
        int ran = rnd.nextInt(mViewRect.right - mWidth*2);
        ran+=mWidth;

    	mRect.offset(ran - mRect.right, -mRect.bottom);

    	mDy = getSpeed();

    	mDrawable = getImg();
	}

	public Rect getBusamenRect(){
		return mRect;
	}

	public int getPlayerWidth(){
		return mWidth;
	}

	public int getPlayerHeight(){
		return mHeight;
	}

	private int getSpeed(){
	    Random rnd = new Random();
        int ran = rnd.nextInt(6);

        return (ran+1);
	}
	private Drawable getImg(){
	    Random rnd = new Random();
	    return mContext.getResources().getDrawable(mBusamenImg[rnd.nextInt(mBusamenImg.length)]);
	}

	public void draw(Canvas canvas) {
		mDrawable.setBounds(mRect);
		mDrawable.draw(canvas);
	}
}
