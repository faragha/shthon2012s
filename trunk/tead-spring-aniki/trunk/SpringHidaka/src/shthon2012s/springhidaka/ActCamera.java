package shthon2012s.springhidaka;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import shthon2012s.springhidaka.Utils.AutoFocusPreview;
import shthon2012s.springhidaka.Utils.DBG;
import shthon2012s.springhidaka.Utils.Params;
import shthon2012s.springhidaka.Utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ActCamera extends Activity {

	private Context ctx;
	private AutoFocusPreview view;
	private RelativeLayout RLpreviewWrap;
	private Button bt_camera;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		ctx = this.getApplicationContext();

		view = new AutoFocusPreview(this, this);

		RLpreviewWrap = (RelativeLayout) findViewById(R.id.rlpreviewInside);

		bt_camera = (Button) findViewById(R.id.bt_camera);

		bt_camera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TakeIt();
			}
		});

		RLpreviewWrap.addView(view);
	}

	protected void TakeIt() {
		view.autoFocus();
	}

	public void CompleteTakePicture() {

	}

	public void GotBitmap(Bitmap bmp) {

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int wh = Math.min(width, height);
		int newWH = 500;

		// calculate the scale - in this case = 0.4f
		float scaleWH = ((float) newWH) / wh;

		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postRotate(90);
		matrix.postScale(scaleWH, scaleWH);
		Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, wh, wh, matrix,
				true);

		SaveForCheck(resizedBitmap);

		Intent it = new Intent(ctx, ActCallList.class);
		startActivity(it);
	}

	private void SaveForCheck(Bitmap resizedBitmap) {
		try {
			byte[] w = bmp2data(resizedBitmap, Bitmap.CompressFormat.JPEG, 80);
			String path = Params.sdPath + Params.folder + Params.tmpfile;
			writeDataFile(path, w);
		} catch (Exception e) {
			android.util.Log.e("", e.toString());
		}
	}

	private static byte[] bmp2data(Bitmap src, Bitmap.CompressFormat format,
			int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		return os.toByteArray();
	}

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////


	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	private void writeDataFile(String name, byte[] w) throws Exception {
		FileOutputStream fos = null;
		try {
			String path = Params.sdPath + Params.folder;
			File f;

			f = new File(path);
			if (Utils.existsFile(path)) {
				if (f.isDirectory()) {
					// OK
				} else {
					f.delete();
				}
			} else {
				f.mkdir();
			}

			path = Params.sdPath + Params.folder + Params.tmpfile;

			f = new File(path);
			if (Utils.existsFile(path)) {
				f.delete();
				f.createNewFile();
			} else {
				f.createNewFile();
			}

			DBG.LogOut(3, "tmp path", ":" + path);
			fos = new FileOutputStream(path);
			fos.write(w);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
