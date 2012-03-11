
package jp.sonicstudio.sutami.image;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.graphics.Bitmap;

/**
 * スタンプ画像作成クラス。
 */
public class StampMaker {

    private static final String TAG = "StampMaker";

    public static final int THRESHOLD_LEVEL_1 = 90;
    public static final int THRESHOLD_LEVEL_2 = 100;
    public static final int THRESHOLD_LEVEL_3 = 110;
    public static final int THRESHOLD_LEVEL_4 = 120;
    public static final int THRESHOLD_LEVEL_5 = 130;
    public static final int DEFAULT_THRESHOLD = THRESHOLD_LEVEL_3;

    private Bitmap mSrcBitmap;
    private Bitmap mDstBitmap;

    public void initialize(Bitmap srcBitmap) {
        mSrcBitmap = srcBitmap;
        mDstBitmap = mSrcBitmap.copy(Bitmap.Config.ARGB_8888, true);
        setHasAlpha(mDstBitmap, true);
    }

    /**
     * <p>
     * Bitmap setHasAlpha() は、API Level 12（Android3.1）以降、<br>
     * （それ以前ではhide）のため、リフレクションで呼び出す
     * </p>
     * 
     * @param bitmap 対象ビットマップ
     * @param hasAlpha アルファをセットするかどうか
     */
    private void setHasAlpha(Bitmap bitmap, boolean hasAlpha) {
        try {
            Class<?> clazz = Bitmap.class;
            Method method = clazz.getDeclaredMethod("setHasAlpha", boolean.class);
            if (method != null) {
                method.invoke(bitmap, hasAlpha);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * スタンプ画像作成処理。<br>
     * 将来的に8近傍参照できるように全画素を取得しているが、<br>
     * 現状の処理だと1行だけあれば処理できるため、メモリ使用量を削減したい場合には変更すること。
     * </p>
     * 
     * @param threshhold　閾値（0から255）
     * @return 作成したスタンプ画像ビットマップ
     */
    public Bitmap process(int threshold) {
        if (mDstBitmap != null) {
            int width = mSrcBitmap.getWidth();
            int height = mSrcBitmap.getHeight();
            int rowBytes = mSrcBitmap.getRowBytes(); // width * BPP
            int bufSize = rowBytes * height;
            int[] pixels = new int[bufSize];
            // Log.v(TAG, "width = " + width);
            // Log.v(TAG, "height = " + height);
            // Log.v(TAG, "rowBytes = " + rowBytes);
            mSrcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            {
                int index = 0;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = pixels[index];
                        int a = (pixel & 0xFF000000) >> 24;
                        int r = (pixel & 0x00FF0000) >> 16;
                        int g = (pixel & 0x0000FF00) >> 8;
                        int b = (pixel & 0x000000FF) >> 0;
                        // グレースケールに変換
                        int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                        if (gray > 255) {
                            gray = 255;
                        }
                        // 閾値を超えた画素は透明にする
                        if (gray > threshold) {
                            a = 0;
                        }
                        int newPixel = (a << 24 | r << 16 | g << 8 | b << 0);
                        pixels[index] = newPixel;
                        index++;
                    }
                }
            }
            mDstBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        }
        return mDstBitmap;
    }

}
