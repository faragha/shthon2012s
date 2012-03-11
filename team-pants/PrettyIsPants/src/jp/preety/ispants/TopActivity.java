package jp.preety.ispants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.preety.ispants.oekaki.OekakiActivity;

/**
 * Top Activity
 * @author pretty is pants
 */
public class TopActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_TAKE_PHOTO = 0x0100;
    private static final int REQUEST_PICK_PHOTO = 0x0200;
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    protected File mCurrentPhotoFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button takePhotoButton = (Button) findViewById(R.id.btn_take_photo);
        final Button pickPhotoButton = (Button) findViewById(R.id.btn_pick_photo);
        final Button joinButton = (Button) findViewById(R.id.btn_join);
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
        // 写真を撮ってラクガキ
            case REQUEST_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    onPhotoSelected(Uri.fromFile(mCurrentPhotoFile));
                }
                break;
            }
            // 写真を選んでラクガキ
            case REQUEST_PICK_PHOTO: {
                if (resultCode == RESULT_OK && data != null) {
                    onPhotoSelected(data.getData());
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
            case R.id.btn_take_photo:
                takePhoto();
                break;
            case R.id.btn_pick_photo:
                pickPhoto();
                break;
            case R.id.btn_join:
                join();
                break;
        }
    }

    /**
     * start take photo intent
     */
    private void takePhoto() {
        mCurrentPhotoFile = getTempFile(PHOTO_DIR, "IMG_", ".jpg");
        final Uri uri = Uri.fromFile(mCurrentPhotoFile);
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri);
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
        final Intent intent = new Intent(this, OekakiActivity.class);
        startActivity(intent);
    }

    /**
     * 写真選択時のイベント
     */
    protected void onPhotoSelected(Uri uri) {
        final Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra(ConfirmActivity.INTENT_IMAGE_URI, uri.toString());
        startActivity(intent);
    }

    /** ファイル名生成 */
    protected static File getTempFile(File dir, String prefix, String suffix) {
        final Date date = new Date(System.currentTimeMillis());
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String name = prefix + sdf.format(date) + suffix;
        dir.mkdirs();
        return new File(dir, name);
    }
}