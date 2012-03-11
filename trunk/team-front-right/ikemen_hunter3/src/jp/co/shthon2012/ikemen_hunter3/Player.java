package jp.co.shthon2012.ikemen_hunter3;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Player {
	// プレイヤー画像
	private Drawable mDrawable;
	private int mWidth;
	private int mHeight;
	private Rect mRect;

	// 表示範囲
	private Rect mViewRect;

	// 移動量
	private double mDx;

	// 加速量
	private final double mAcceleration = 3;

	// 減衰量
	private final double mAttenuator = 0.02;

	// 壁のはね返り量
	private final double mBound = 0.9;

	public Player(Drawable drawable, Rect viewRect) {
		// 画像を設定
		mDrawable = drawable;
	    mWidth = drawable.getIntrinsicWidth();
	    mHeight = drawable.getIntrinsicHeight();
	    mRect = new Rect(0, 0, mWidth, mHeight);

	    // 表示範囲を設定
	    mViewRect = viewRect;

	    // 初期位置を設定
	    mRect.offset(
	    	(mViewRect.right - mViewRect.left - mWidth) / 2,
	    	(mViewRect.bottom - mHeight - 10));

	    // 移動量を初期化
	    mDx = 0;
	}

	public void move(float mPitch) {
		// 傾きから移動量の変動幅を計算
		double dx = - Math.sin(Math.toRadians(mPitch)) * mAcceleration;

		// 傾きによって移動量を修正
		mDx += mDx * -mAttenuator + dx;

		// プレイヤーを移動
	    mRect.offset((int)mDx, 0);

	    // 表示範囲の枠に当たったらはね返す
	    if (mRect.left < 0) {
	        mDx = -mDx * mBound;
	        mRect.left = 0;
	        mRect.right = mRect.left + mWidth;
	    } else if (mRect.right > mViewRect.right) {
	        mDx = -mDx * mBound;
	        mRect.right = mViewRect.right;
	        mRect.left = mRect.right - mWidth;
	    }
	}

	public Rect getPlayerRect(){
		return mRect;
	}

	public int getPlayerWidth(){
		return mWidth;
	}

	public int getPlayerHeight(){
		return mHeight;
	}

	public void draw(Canvas canvas) {
		mDrawable.setBounds(mRect);
		mDrawable.draw(canvas);
	}
}
