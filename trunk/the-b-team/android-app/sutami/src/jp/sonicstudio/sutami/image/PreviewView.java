
package jp.sonicstudio.sutami.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 画像プレビュービュークラス
 */
public class PreviewView extends View {

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

}
