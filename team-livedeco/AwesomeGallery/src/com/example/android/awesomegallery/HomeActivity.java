package com.example.android.awesomegallery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.awesomegallery.LoadImageListTask.ImageLoadCallback;

public class HomeActivity extends Activity {

	public static final String LOG_TAG = HomeActivity.class.getSimpleName();

	public static final String EXTRA_IMAGE_ARRAY = "image_list";

	private GridView mImageGridView;

	private ImageAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		initImageGridView();
		initOKButton();
		initCancelButton();

		LoadImageListTask task = new LoadImageListTask(this,
				new ImageLoadCallback() {
					@Override
					public void onLoadImages(List<Long> imageIdList) {
						mAdapter.setImageIdList(imageIdList);
						mAdapter.notifyDataSetChanged();
					}
				});
		task.execute();
	}

	private void initImageGridView() {
		mAdapter = new ImageAdapter(this);
		mImageGridView = (GridView) findViewById(R.id.image_grid_view);
		mImageGridView.setAdapter(mAdapter);
		mImageGridView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						mAdapter.toggleChecked(position);
						mAdapter.notifyDataSetChanged();
						return true;
					}
				});
	}

	private void initOKButton() {
		Button button = (Button) findViewById(R.id.ok_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri[] uriArray = mAdapter.getSelectedList();
				Intent data = new Intent();
				data.putExtra(EXTRA_IMAGE_ARRAY, uriArray);
				setResult(RESULT_OK, data);
				finish();
			}
		});
	}

	private void initCancelButton() {
		Button button = (Button) findViewById(R.id.cancel_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	private static class ImageAdapter extends BaseAdapter {

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
				imageView.setPadding(8, 8, 8, 8);
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
				imageView.setBackgroundResource(R.drawable.background);
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

		public Uri[] getSelectedList() {
			List<Uri> list = new ArrayList<Uri>(
					mSelectedImageIdCollection.size());
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
}
