package shthon2012s.springhidaka.Utils;

import android.util.Log;

public class DBG {
	public static final boolean debug_flg = false;

	public static void LogOut(int lv, String keystr, String str) {
		/*
		 */
		if (debug_flg) {
			if (lv == 0) {
				Log.d(keystr, str);
			} else if (lv == 1) {
				Log.i(keystr, str);
			} else if (lv == 2) {
				Log.w(keystr, str);
			} else if (lv == 3) {
				Log.e(keystr, str);
			}
		}

	}

}
