package jp.sonicstudio.sutami.image;

import android.graphics.Bitmap;

public class StampMaker {

	// private static final String TAG = "StampMaker";

	public static final int THRESHHOLD_LEVEL_1 = 64;
	public static final int THRESHHOLD_LEVEL_2 = 96;
	public static final int THRESHHOLD_LEVEL_3 = 128;
	public static final int THRESHHOLD = THRESHHOLD_LEVEL_3;

	private Bitmap mSrcBitmap;
	private Bitmap mDstBitmap;

	public void initialize(Bitmap srcBitmap) {
		mSrcBitmap = srcBitmap;
		mDstBitmap = mSrcBitmap.copy(Bitmap.Config.ARGB_8888, true);
	}

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
						int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
						if (gray > 255) {
							gray = 255;
						}
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