package jp.preety.ispants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Top Activity
 * @author pretty is pants
 */
public class TopActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_TAKE_PHOTO = 0x0100;
    private static final int REQUEST_PICK_PHOTO = 0x0200;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ImageView takePhotoButton = (ImageView)findViewById(R.id.btn_take_photo);
        final ImageView pickPhotoButton = (ImageView)findViewById(R.id.btn_pick_photo);
        final ImageView joinButton = (ImageView)findViewById(R.id.btn_join);
        takePhotoButton.setOnClickListener(this);
        pickPhotoButton.setOnClickListener(this);
        joinButton.setOnClickListener(this);
    }
    
    /**
     * Event Handler: on receive activity result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
            case REQUEST_PICK_PHOTO: {
                if (resultCode == RESULT_OK && data != null) {
                    final String timestamp = String.valueOf(System.currentTimeMillis());
                    final Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    final String uri = MediaStore.Images.Media.insertImage(
                            getContentResolver(), bitmap, timestamp, timestamp);
                    Toast.makeText(this, uri, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
    
    /**
     * Event Hanlder: on click view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo: takePhoto(); break;
            case R.id.btn_pick_photo: pickPhoto(); break;
            case R.id.btn_join: join(); break;
        }
    }
    
    /**
     * start take photo intent
     */
    private void takePhoto() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }
    
    /**
     * start pick photo intent
     */
    private void pickPhoto() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }
    
    /**
     * start join intent
     */
    private void join() {
        // トモダチのラクガキに参加する
    }
}