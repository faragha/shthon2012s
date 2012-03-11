package jp.co.shthon2012.majiccamera;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraPreviewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout l = new LinearLayout(this);
        l.addView(new cameraView(this));
//        l.setLayoutParams(new LayoutParams(640,480));
        setContentView(l);

		Toast.makeText(this, "横持ちで撮ってね", Toast.LENGTH_LONG).show();
    }

	private class cameraView extends SurfaceView implements Callback, PictureCallback {
    	private Camera camera;
    	Context mContext;

    	public cameraView(Context context) {
    		super(context);
    		SurfaceHolder holder = getHolder();
    		holder.addCallback(this);
    		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    		mContext = context;
    	}

    	@Override
    	public void surfaceCreated(SurfaceHolder holder) {
    		try {
    			camera = Camera.open();
    			camera.setPreviewDisplay(holder);
    		} catch(IOException e) {
    		}
    	}

    	@Override
    	public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
    		Camera.Parameters parameters = camera.getParameters();
    		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

    		Camera.Size size = sizes.get(0);
    		parameters.setPreviewSize(size.width, size.height);
    		parameters.setPreviewSize(size.width, size.height);
    		camera.setParameters(parameters);
    		// カメラプレビューの開始
    		camera.startPreview();
    	}

    	@Override
    	public void surfaceDestroyed(SurfaceHolder holder) {
    		camera.stopPreview();
    		camera.release();
    	}

    	@Override
    	public void onPictureTaken(byte[] data, Camera c) {
    		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, null);
    		MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, "", null);

            Intent intent;
            intent = new Intent(mContext, MajicPreviewActivity.class);
            startActivity(intent);
    	}

    	@Override
    	public boolean onTouchEvent(MotionEvent me) {
    		if(me.getAction()==MotionEvent.ACTION_DOWN) {
    			camera.takePicture(null,null,this);
    		}
    		return true;
    	}
	}
}
