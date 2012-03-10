
package jp.sonicstudio.sutami.image;

import android.graphics.Bitmap;

/**
 * スタンプ画像作成クラス。
 */
public class StampMaker {

    private static final String TAG = "StampMaker";

    public static final int THRESHHOLD_LEVEL_1 = 120;
    public static final int THRESHHOLD_LEVEL_2 = 124;
    public static final int THRESHHOLD_LEVEL_3 = 128;
    public static final int THRESHHOLD_LEVEL_4 = 132;
    public static final int THRESHHOLD_LEVEL_5 = 136;
    public static final int DEFAULT_THRESHHOLD = THRESHHOLD_LEVEL_3;

    private Bitmap mSrcBitmap;
    private Bitmap mDstBitmap;

    public void initialize(Bitmap srcBitmap) {
        mSrcBitmap = srcBitmap;
        mDstBitmap = mSrcBitmap.copy(Bitmap.Config.ARGB_8888, true);
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
    public Bitmap process(int threshhold) {
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
                        if (gray > threshhold) {
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
