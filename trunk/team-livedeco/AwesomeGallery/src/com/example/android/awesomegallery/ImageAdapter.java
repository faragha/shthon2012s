package com.example.android.awesomegallery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
			// imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		Long id = mImageIdList.get(position);
		Bitmap thumbnail = Thumbnails.getThumbnail(mContentResolver,
				id.longValue(), Thumbnails.MICRO_KIND, null);
		imageView.setImageBitmap(thumbnail);

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
}