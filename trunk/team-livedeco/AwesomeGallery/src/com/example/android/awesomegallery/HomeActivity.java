package com.example.android.awesomegallery;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;

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
				Uri[] uris = mAdapter.getSelectedUris();
				Intent data = new Intent();
				data.putExtra(EXTRA_IMAGE_ARRAY, uris);
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
}
