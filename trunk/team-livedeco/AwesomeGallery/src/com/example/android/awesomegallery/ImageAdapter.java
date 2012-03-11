package com.example.android.awesomegallery;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;

	private ContentResolver mContentResolver;

	private List<Long> mImageIdList = new ArrayList<Long>();

	private Set<Integer> mSelectedImageIdCollection = new HashSet<Integer>();

	private Map<Long, SoftReference<Bitmap>> mThumbnailCacheHolder = new HashMap<Long, SoftReference<Bitmap>>();

	private Map<Long, LoadImageTask> mLoadingImageTaskHolder = new HashMap<Long, ImageAdapter.LoadImageTask>();

	public ImageAdapter(Context context) {
		mContext = context;
		mContentResolver = context.getContentResolver();
	}

	@Override
	public int getCount() {
		return mImageIdList.size();
	}

	@Override
	public Object getItem(int position) {
		return mImageIdList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(90, 90));
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		Long id = mImageIdList.get(position);
		Bitmap bitmap = null;
		if (mThumbnailCacheHolder.containsKey(id)) {
			SoftReference<Bitmap> bitmapRef = mThumbnailCacheHolder.get(id);
			bitmap = bitmapRef.get();
		}
		if (bitmap == null) {
			// imageView.setImageDrawable(mCatProgress);
			imageView.setImageResource(R.drawable.cat_progress);

			synchronized (mLoadingImageTaskHolder) {
				if (!mLoadingImageTaskHolder.containsKey(id)) {
					try {
						LoadImageTask task = new LoadImageTask();
						mLoadingImageTaskHolder.put(id, task);
						task.execute(id);
					} catch (RejectedExecutionException e) {
						mLoadingImageTaskHolder.remove(id);
					}
				}
			}
		} else {
			imageView.setImageBitmap(bitmap);
		}

		if (mSelectedImageIdCollection.contains(position)) {
			imageView.setBackgroundResource(R.drawable.background_selected);
		} else {
			imageView.setBackgroundResource(R.drawable.background_normal);
		}

		return imageView;
	}

	public void setImageIdList(List<Long> imageIdList) {
		mImageIdList.clear();
		mImageIdList.addAll(imageIdList);
		mSelectedImageIdCollection.clear();
		mThumbnailCacheHolder.clear();
	}

	public void toggleChecked(int index) {
		if (mSelectedImageIdCollection.contains(index)) {
			mSelectedImageIdCollection.remove(index);
		} else {
			mSelectedImageIdCollection.add(index);
		}
	}

	public Uri[] getSelectedUris() {
		List<Uri> list = new ArrayList<Uri>(mSelectedImageIdCollection.size());
		for (Integer index : mSelectedImageIdCollection) {
			Long id = mImageIdList.get(index.intValue());
			String uriString = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
					+ "/" + id.toString();
			Uri uri = Uri.parse(uriString);
			list.add(uri);
		}
		return list.toArray(new Uri[list.size()]);
	}

	private class LoadImageTask extends AsyncTask<Long, Void, Bitmap> {

		private Long mId;

		@Override
		protected Bitmap doInBackground(Long... params) {
			mId = params[0];
			Bitmap thumbnail = Thumbnails.getThumbnail(mContentResolver,
					mId.longValue(), Thumbnails.MINI_KIND, null);
			return thumbnail;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (mThumbnailCacheHolder.containsKey(mId)) {
				mThumbnailCacheHolder.remove(mId);
			}
			mThumbnailCacheHolder.put(mId, new SoftReference<Bitmap>(result));
			synchronized (mLoadingImageTaskHolder) {
				mLoadingImageTaskHolder.remove(mId);
			}
			notifyDataSetChanged();
		}
	}
}