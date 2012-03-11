package com.example.android.awesomegallery;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class CatActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cat);

		// initCatImageView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// initCatImageView();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		initCatImageView();
	}

	private void initCatImageView() {
		ImageView view = (ImageView) findViewById(R.id.cat_image_view);
		AnimationDrawable drawable = (AnimationDrawable) getResources()
				.getDrawable(R.drawable.cat_progress);
		// view.setBackgroundDrawable(drawable);
		view.setImageDrawable(drawable);
		drawable.start();

	}

}
