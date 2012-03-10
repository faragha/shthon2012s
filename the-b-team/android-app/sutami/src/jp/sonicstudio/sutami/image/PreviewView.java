package jp.sonicstudio.sutami.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class PreviewView extends View {

	private String mImagePath;
	private Bitmap mBitmap;
	private int mViewWidth;
	private int mViewHeight;

	public PreviewView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PreviewView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PreviewView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmap != null) {
			float scaleWidth = (float) mViewWidth / mBitmap.getWidth();
			float scaleHeight = (float) mViewHeight / mBitmap.getHeight();
			float scale = Math.min(scaleWidth, scaleHeight);
			int scaledWidth = (int) (scale * mBitmap.getWidth());
			int scaledHeight = (int) (scale * mBitmap.getHeight());
			int offsetX = (mViewWidth - scaledWidth) / 2; 
			int offsetY = 0; // (mViewHeight - scaledHeight) / 2; // 上寄せ
			Paint paint = new Paint();
			Matrix matrix = new Matrix();
			matrix.setScale(scale, scale);
			canvas.translate(offsetX, offsetY);
			canvas.drawBitmap(mBitmap, matrix, paint);
		}
	}
	
	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
		invalidate();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mViewWidth = w;
		mViewHeight= h;
		invalidate();
	}
	
}
