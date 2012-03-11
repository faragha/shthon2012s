
package jp.sonicstudio.sutami.image;

import jp.sonicstudio.sutami.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 画像プレビュービュークラス
 */
public class PreviewView extends View {

    private static final int RECT_LINE_SIZE = 5;
    private static final String COLOR_RECT_LINE = "#E100E5";
    private static final int SELECT_LINE_MARGE = RECT_LINE_SIZE * 2;
    
    private Bitmap mBitmap;
    private int mViewWidth;
    private int mViewHeight;
    private Bitmap mCloseImage;
    private boolean mShowRectFlag = false;
    private RectF mRectf = new RectF(0, 0, 0, 0) ;
    


    public PreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mCloseImage = BitmapFactory.decodeResource(getResources(),R.drawable.close);
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mCloseImage = BitmapFactory.decodeResource(getResources(),R.drawable.close);
    }

    public PreviewView(Context context) {
        super(context);
        
        mCloseImage = BitmapFactory.decodeResource(getResources(),R.drawable.close);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            // アスペクト比を保ってビューにフィットする拡大縮小率を計算
            float scaleWidth = (float) mViewWidth / mBitmap.getWidth();
            float scaleHeight = (float) mViewHeight / mBitmap.getHeight();
            float scale = Math.min(scaleWidth, scaleHeight);
            // ビュー内でセンタリングするためのオフセットを計算
            int offsetX = (mViewWidth - (int) (scale * mBitmap.getWidth())) / 2;
            int offsetY = (mViewHeight - (int) (scale * mBitmap.getHeight())) / 2;
            // ビットマップを描画
            Paint paint = new Paint();
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            
            canvas.save();
            canvas.translate(offsetX, offsetY);
            canvas.drawBitmap(mBitmap, matrix, paint);
            canvas.restore();
            
            drawSelectRect(canvas);
            
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        invalidate();
    }

    /**
     * ビューにビットマップを設定する
     * 
     * @param bitmap 表示したい画像
     */
    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }

    
    private float closeImageX = 0;
    private float closeImageY = 0;
    

    
    public void drawSelectRect(Canvas canvas){
	
	if(mRectf.left != 0 || mRectf.top != 0 || mRectf.right !=0 || mRectf.bottom != 0){
	    Paint paint = new Paint();
	    paint.setColor(Color.parseColor(COLOR_RECT_LINE)); 
	    paint.setStrokeWidth(RECT_LINE_SIZE);
	    paint.setStyle(Style.STROKE);
	    canvas.drawRect(mRectf, paint);
	}
	
	if (mShowRectFlag) {
	    Paint closePaint = new Paint();
	    closeImageX = mRectf.right - mCloseImage.getWidth() / 2;
	    closeImageY = mRectf.top - mCloseImage.getHeight() + mCloseImage.getHeight() / 2;

	    if (closeImageX > getWidth()) {
		closeImageX = getWidth() - mCloseImage.getWidth() - RECT_LINE_SIZE - 20;
	    }

	    canvas.drawBitmap(mCloseImage, closeImageX, closeImageY, closePaint);
	}
    }
    
    private static final int SELECT_LINE_LEFT = 1;
    private static final int SELECT_LINE_TOP = 2;
    private static final int SELECT_LINE_RIGHT = 3;
    private static final int SELECT_LINE_BOTTOM = 4;
    private int mSelectLineNum = 0;
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

	switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
		Log.d("me","ACTION_DOWN");
		if(!mShowRectFlag){
		    // 選択の枠始点設定
		    mRectf.left = event.getX();
		    mRectf.top = event.getY();
		    mRectf.right = event.getX();
		    mRectf.bottom = event.getY();
		}else{

		    float x =  event.getX();
		    float y = event.getY();

		    //選択された線を記憶
		    if(isSelectLineLeft(x)){
			mSelectLineNum = SELECT_LINE_LEFT;
		    }else 
		    
		    if(isSelectLineRight(x)){
			mSelectLineNum = SELECT_LINE_RIGHT;
		    }else		    
		    
		    if(isSelectTop(y)){
			mSelectLineNum = SELECT_LINE_TOP;
		    }else	
	
		    if(isSelectBottom(y)){
			mSelectLineNum = SELECT_LINE_BOTTOM; 
		    }
		}
		
		
	        break;
	    case MotionEvent.ACTION_UP:
		if(!mShowRectFlag){
		    
		    Log.d("me","ACTION_UP");
		    // 選択の枠終点指定
		    mRectf.right = event.getX();
		    mRectf.bottom = event.getY();
		    
		    mShowRectFlag = true;
		    exchangePoints();
		    invalidate();
		}else{
		    float  x= event.getX();
		    float y =  event.getY();
		    if(closeImageX < x && x < closeImageX + mCloseImage.getWidth() && closeImageY < y && y < closeImageY + mCloseImage.getHeight()){
			// 閉じるボタンを押した
			mRectf.set(0, 0, 0, 0);
			mShowRectFlag = false;
			invalidate();
		    }else{
			setSelectLineMovePoint(event);
			exchangePoints();
			mSelectLineNum = 0;
		    }
		}
		    
		
		
	        break;
	    case MotionEvent.ACTION_MOVE:
		Log.d("me","ACTION_MOVE");
		if(!mShowRectFlag){
		    // 選択の枠終点指定
		    mRectf.right = event.getX();
		    mRectf.bottom = event.getY();
		    
		}else{
		    setSelectLineMovePoint(event);
		}
		invalidate();
	        break;
	    }
	
	return true;
    }

    private void setSelectLineMovePoint(MotionEvent event) {
	switch (mSelectLineNum) {
	case SELECT_LINE_LEFT:
	    mRectf.left = event.getX();
	    
	    invalidate();
	    break;

	case SELECT_LINE_TOP:
	    mRectf.top = event.getY();
	    
	    invalidate();
	    break;

	case SELECT_LINE_RIGHT:
	    mRectf.right = event.getX();
	    
	    invalidate();
	    break;

	case SELECT_LINE_BOTTOM:
	    mRectf.bottom = event.getY();
	    invalidate();
	    break;

	    
	default:
	    break;
	}
    }
    
    private void exchangePoints(){
	
	
	if(mRectf.right < mRectf.left){
	    float tmp = mRectf.left;
	    mRectf.left = mRectf.right;
	    mRectf.right = tmp;
	}
	if(mRectf.bottom < mRectf.top){
	    float tmp = mRectf.top;
	    mRectf.top = mRectf.bottom;
	    mRectf.bottom = tmp;
	}
	if(mRectf.left < RECT_LINE_SIZE){
	    mRectf.left = RECT_LINE_SIZE;
	}
	if(mRectf.top < RECT_LINE_SIZE){
	    mRectf.top = RECT_LINE_SIZE;
	}
	if(mRectf.right > getWidth() -RECT_LINE_SIZE){
	    mRectf.right =  getWidth() -RECT_LINE_SIZE;
	}
	if(mRectf.bottom > getHeight() -RECT_LINE_SIZE){
	    mRectf.bottom =  getHeight() -RECT_LINE_SIZE;
	}
	
	
    }

    private boolean isSelectLineLeft(float x) {
	if (mRectf.left - SELECT_LINE_MARGE <= x && x <= mRectf.left + SELECT_LINE_MARGE) {
	    return true;
	}
	return false;
    }

    private boolean isSelectLineRight(float x) {
	if (mRectf.right - SELECT_LINE_MARGE <= x && x <= mRectf.right + SELECT_LINE_MARGE) {
	    return true;
	}

	return false;
    }

    private boolean isSelectTop(float y) {
	if (mRectf.top - SELECT_LINE_MARGE <= y && y <= mRectf.top + SELECT_LINE_MARGE) {
	    return true;
	}
	return false;
    }

    private boolean isSelectBottom(float y) {
	if (mRectf.bottom - SELECT_LINE_MARGE <= y && y <= mRectf.bottom + SELECT_LINE_MARGE) {
	    return true;
	}
	return false;
    }
    
    public Bitmap getSelectImage(){
	
	if(!mShowRectFlag){
	    return mBitmap;

	}
        
        Log.d("me","Select x="  +  mRectf.left +" y=" + mRectf.top + "xx="+mRectf.right +" yy=" + mRectf.bottom);
	Log.d("me","mBitmap w=" + mBitmap.getWidth() + " h=" + mBitmap.getHeight());        
        Log.d("me","Width="  +  getWidth() +" height=" + getHeight());
        
        
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // アスペクト比を保ってビューにフィットする拡大縮小率を計算
        float scaleWidth = (float) mViewWidth / mBitmap.getWidth();
        float scaleHeight = (float) mViewHeight / mBitmap.getHeight();
        float scale = Math.min(scaleWidth, scaleHeight);
        // ビュー内でセンタリングするためのオフセットを計算
        int offsetX = (mViewWidth - (int) (scale * mBitmap.getWidth())) / 2;
        int offsetY = (mViewHeight - (int) (scale * mBitmap.getHeight())) / 2;
        // ビットマップを描画
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.drawBitmap(mBitmap, matrix, paint);
        canvas.restore();
        
	return Bitmap.createBitmap(bitmap, (int)mRectf.left, (int)mRectf.top, (int)mRectf.width(), (int)mRectf.height());
    }
    
}
