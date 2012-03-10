package com.example.android.awesomegallery;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

public class LoadImageListTask extends AsyncTask<Void, Void, List<Long>> {

	public static final String LOG_TAG = LoadImageListTask.class
			.getSimpleName();

	private Context mContext;

	private ImageLoadCallback mCallback;

	private ProgressDialog mProgressDialog;

	public LoadImageListTask(Context context, ImageLoadCallback callback) {
		mContext = context;
		mCallback = callback;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("loading...");
	}

	@Override
	protected void onPreExecute() {
		Log.d(LOG_TAG, "show progress dialog ");
		mProgressDialog.show();
	}

	@Override
	protected List<Long> doInBackground(Void... params) {
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
				null);
		List<Long> imageIdList = new ArrayList<Long>();
		while (cursor.moveToNext()) {
			long id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			imageIdList.add(id);
		}
		return imageIdList;
	}

	@Override
	protected void onPostExecute(List<Long> imageIdList) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			Log.d(LOG_TAG, "dissmiss progress dialog ");
			mProgressDialog.dismiss();
		}

		mCallback.onLoadImages(imageIdList);
	}

	public interface ImageLoadCallback {
		void onLoadImages(List<Long> imageIdList);
	}
}