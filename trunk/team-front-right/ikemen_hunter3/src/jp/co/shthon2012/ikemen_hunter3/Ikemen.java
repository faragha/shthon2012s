package jp.co.shthon2012.ikemen_hunter3;

import java.util.Random;

import jp.co.shthon2012.ikemen_counterattack.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Ikemen {
	Context mContext;
	// イケメン画像
	private Drawable mDrawable;
	private int mWidth;
	private int mHeight;
	private Rect mRect;
	private int[] mIkemenImg = {R.drawable.b01, R.drawable.b03};

	// 表示範囲
	private Rect mViewRect;

	// 移動量
	private double mDy = 3;

	public Ikemen(Context context, Rect viewRect) {
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
		// イケメンを移動
	    if(mRect.bottom >= mViewRect.bottom){
			MainSurfaceView.setIkemenMiss();
	    }else{
	    	mRect.offset(0, (int)mDy);
	    }
	}

	public void restartIkemen(){
	    //X軸の位置を指定
	    Random rnd = new Random();
        int ran = rnd.nextInt(mViewRect.right - mWidth*2);
        ran+=mWidth;

    	mRect.offset(ran - mRect.right, -mRect.bottom);

    	//スピード変更
    	mDy = getSpeed();

    	//画像変更
		Drawable drawable = getImg();
		mDrawable = drawable;
	}

	public Rect getIkemenRect(){
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
        int ran = rnd.nextInt(4);

        return (ran+3);
	}

	private Drawable getImg(){
	    Random rnd = new Random();
	    return mContext.getResources().getDrawable(mIkemenImg[rnd.nextInt(mIkemenImg.length)]);
	}

	public void draw(Canvas canvas) {
		mDrawable.setBounds(mRect);
		mDrawable.draw(canvas);
	}
}
