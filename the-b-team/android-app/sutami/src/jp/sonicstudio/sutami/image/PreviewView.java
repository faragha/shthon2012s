
package jp.sonicstudio.sutami.image;

import jp.sonicstudio.sutami.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 画像プレビュービュークラス
 */
public class PreviewView extends View {

    private static final int RECT_LINE_SIZE = 5;
    private Bitmap mBitmap;
    private int mViewWidth;
    private int mViewHeight;
    private Bitmap mCloseImage;
    
    private static final String COLOR_RECT_LINE = "#E100E5";

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
    
    
    
    private float selectRectX = 0;
    private float selectRectY = 0;
    
    private float selectRectXX = 0;
    private float selectRectYY = 0;
    
    private boolean rectSetFlag = false;
    
    private float closeImageX = 0;
    private float closeImageY = 0;
    
    public void drawSelectRect(Canvas canvas){
	
	if(selectRectX != 0 || selectRectY != 0 || selectRectXX != 0 || selectRectYY !=0 ){
	    Paint paint = new Paint();
	    paint.setColor(Color.parseColor(COLOR_RECT_LINE)); 
	    paint.setStrokeWidth(RECT_LINE_SIZE);
	    paint.setStyle(Style.STROKE);
	    canvas.drawRect(selectRectX, selectRectY, selectRectXX, selectRectYY, paint);
	}
	
	if(rectSetFlag){
	    Paint closePaint = new Paint();
	    closeImageX = selectRectXX - mCloseImage.getWidth()/2;
	    closeImageY = selectRectY - mCloseImage.getHeight() + mCloseImage.getHeight()/2;
	    
	    if(closeImageX > getWidth()){
		closeImageX =  getWidth() - mCloseImage.getWidth() - RECT_LINE_SIZE -20;
	    }
	    
	    
	    
	    canvas.drawBitmap(mCloseImage, closeImageX,closeImageY, closePaint);
	
	} 
	
	
	
    }
    
    private static final int SELECT_LINE_X = 1;
    private static final int SELECT_LINE_Y = 2;
    private static final int SELECT_LINE_XX = 3;
    private static final int SELECT_LINE_YY = 4;
    private int mSelectLineNum = 0;
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

	switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
		Log.d("me","ACTION_DOWN");
		if(!rectSetFlag){
		    // 選択の枠始点設定
		    selectRectX = event.getX();
		    selectRectY = event.getY();
		    selectRectXX = event.getX();
		    selectRectYY = event.getY();
		}else{

		    float x =  event.getX();
		    float y = event.getY();

		    //X線
		    if(isSelectLineX(x)){
			mSelectLineNum = SELECT_LINE_X;
		    }else 
		    
		    if(isSelectLineXX(x)){
			mSelectLineNum = SELECT_LINE_XX;
		    }else		    
		    
		    if(isSelectY(y)){
			mSelectLineNum = SELECT_LINE_Y;
		    }else	
	
		    if(isSelectYY(y)){
			mSelectLineNum = SELECT_LINE_YY; 
		    }
		}
		
		
	        break;
	    case MotionEvent.ACTION_UP:
		if(!rectSetFlag){
		    
		    Log.d("me","ACTION_UP");
		    // 選択の枠終点指定
		    selectRectXX = event.getX();
		    selectRectYY = event.getY();
		    
		    rectSetFlag = true;
		    exchangePoints();
		    invalidate();
		}else{
		    float  x= event.getX();
		    float y =  event.getY();
		    if(closeImageX < x && x < closeImageX + mCloseImage.getWidth() && closeImageY < y && y < closeImageY + mCloseImage.getHeight()){
			    // 閉じるボタンを押した
			    selectRectX = 0;
			    selectRectY = 0;
			    selectRectXX = 0;
			    selectRectYY = 0;
			    rectSetFlag = false;
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
		if(!rectSetFlag){
		    // 選択の枠終点指定
		    
		    selectRectXX = event.getX();
		    selectRectYY = event.getY();
		    
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
	case SELECT_LINE_X:
	    selectRectX = event.getX();
	    
	    invalidate();
	    break;

	case SELECT_LINE_Y:
	    selectRectY = event.getY();
	    
	    invalidate();
	    break;

	case SELECT_LINE_XX:
	    selectRectXX = event.getX();
	    
	    invalidate();
	    break;

	case SELECT_LINE_YY:
	    selectRectYY = event.getY();
	    invalidate();
	    break;

	    
	default:
	    break;
	}
    }
    
    private void exchangePoints(){
	if(selectRectXX < selectRectX){
	    float tmp = selectRectX;
	    selectRectX = selectRectXX;
	    selectRectXX = tmp;
	}
	if(selectRectYY < selectRectY){
	    float tmp = selectRectY;
	    selectRectY = selectRectYY;
	    selectRectYY = tmp;
	}
	if(selectRectX < RECT_LINE_SIZE){
	    selectRectX = RECT_LINE_SIZE;
	}
	if(selectRectY < RECT_LINE_SIZE){
	    selectRectY = RECT_LINE_SIZE;
	}
	if(selectRectXX > getWidth() -RECT_LINE_SIZE){
	    selectRectXX =  getWidth() -RECT_LINE_SIZE;
	}
	if(selectRectYY > getHeight() -RECT_LINE_SIZE){
	    selectRectYY =  getHeight() -RECT_LINE_SIZE;
	}
	
	
    }

    private boolean isSelectLineX(float x) {
	if (selectRectX - RECT_LINE_SIZE * 2 <= x && x <= selectRectX + RECT_LINE_SIZE * 2) {
	    return true;
	}
	return false;
    }

    private boolean isSelectLineXX(float x) {
	if (selectRectXX - RECT_LINE_SIZE * 2 <= x && x <= selectRectXX + RECT_LINE_SIZE * 2) {
	    return true;
	}

	return false;
    }

    private boolean isSelectY(float y) {
	if (selectRectY - RECT_LINE_SIZE * 2 <= y && y <= selectRectY + RECT_LINE_SIZE * 2) {
	    return true;
	}
	return false;
    }

    private boolean isSelectYY(float y) {
	if (selectRectYY - RECT_LINE_SIZE * 2 <= y && y <= selectRectYY + RECT_LINE_SIZE * 2) {
	    return true;
	}
	return false;
    }
    
    public Bitmap getSelectImage(){
	
	if(!rectSetFlag){
	    return mBitmap;
	}
        
        Log.d("me","mBitmap w=" + mBitmap.getWidth() + " h=" + mBitmap.getHeight());
        Log.d("me","Select x="  +  selectRectX +" y=" + selectRectY + "xx="+selectRectXX +" yy=" + selectRectYY);
        
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
        
	return Bitmap.createBitmap(bitmap, (int)(selectRectX), (int)(selectRectY), (int)(selectRectXX -selectRectX), (int)(selectRectYY - selectRectY));
    }
    
}
