
package jp.sonicstudio.sutami.activity;

import jp.sonicstudio.sutami.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class SutamiActivity extends Activity {
    private static final int MESSAGE_START_ANIMATION_0 = 0;

    private static final int DELAY_START_ANIMATION_0 = 100;

    private static final int MESSAGE_START_ANIMATION_1 = 1;

    private static final int DELAY_START_ANIMATION_1 = 500;

    private static final int MESSAGE_START_ANIMATION_2 = 2;

    private static final int DELAY_START_ANIMATION_2 = 500;

    private static final int MESSAGE_START_ANIMATION_3 = 3;

    private static final int DELAY_START_ANIMATION_3 = 500;

    private static final int REQUEST_GET_CONTENT = 0;

    private static final String TAG = "SutamiActivity";

    private final static int REQUEST_GET_CAMERA_IMAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.button_taka_a_picture).setOnClickListener(mTakeAPictureOnClickListener);
        findViewById(R.id.button_pick_a_picture).setOnClickListener(
                mPickAPictureFromGarallyOnClickListener);
    }

    private Uri mCameraImageUri;

    private View.OnClickListener mTakeAPictureOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            ContentValues values = new ContentValues();

            String filename = System.currentTimeMillis() + ".jpg";
            values.put(MediaStore.Images.Media.TITLE, filename);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            // 保存先
            mCameraImageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent();
            // インテントにアクションをセット
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
            // カメラアプリ起動
            startActivityForResult(intent, REQUEST_GET_CAMERA_IMAGE);
        }
    };

    private View.OnClickListener mPickAPictureFromGarallyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // ギャラリーから画像を取得する
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_GET_CONTENT);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean handeled = false;
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_GET_CONTENT:
                    try {
                        Uri uri = data.getData();
                        Intent intent = new Intent(this, PreviewActivity.class);
                        intent.putExtra(PreviewActivity.IMAGE_URI, uri);
                        startActivity(intent);
                        handeled = true;
                    } catch (Exception e) {
                    }
                    break;
                case REQUEST_GET_CAMERA_IMAGE:

                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                    }

                    // URIを取得できない場合、Intent生成時のURIを参照先に設定
                    if (uri == null) {
                        uri = mCameraImageUri;
                    }

                    Intent intent = new Intent(this, PreviewActivity.class);
                    intent.putExtra(PreviewActivity.IMAGE_URI, uri);
                    startActivity(intent);
                    handeled = true;
                    break;
            }
        if (!handeled) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            View imageTop = findViewById(R.id.image_top);
            View imageBottom = findViewById(R.id.image_bottom);
            View buttonTakeAPicture = findViewById(R.id.button_taka_a_picture);
            View buttonPickAPictureFromGalrally = findViewById(R.id.button_pick_a_picture);
            View imageRabbit = findViewById(R.id.image_rabbit);
            View imageCat = findViewById(R.id.image_cat);

            if (msg.what == MESSAGE_START_ANIMATION_0) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_START_ANIMATION_1, DELAY_START_ANIMATION_0);
            } else if (msg.what == MESSAGE_START_ANIMATION_1) {
                float imageTopDelta = -imageTop.getHeight();
                float imageBottomDelta = imageBottom.getHeight();

                imageTop.setVisibility(View.VISIBLE);
                imageBottom.setVisibility(View.VISIBLE);
                { // トップ画像のアニメーション
                    Animation anim = new TranslateAnimation(0, 0, imageTopDelta, 0);
                    anim.setDuration(DELAY_START_ANIMATION_1);
                    imageTop.startAnimation(anim);
                }
                { // ボトム画像のアニメーション
                    Animation anim = new TranslateAnimation(0, 0, imageBottomDelta, 0);
                    anim.setDuration(DELAY_START_ANIMATION_1);
                    imageBottom.startAnimation(anim);
                }
                mHandler.sendEmptyMessageDelayed(MESSAGE_START_ANIMATION_2, DELAY_START_ANIMATION_2);
            } else if (msg.what == MESSAGE_START_ANIMATION_2) {
                buttonTakeAPicture.setVisibility(View.VISIBLE);
                buttonPickAPictureFromGalrally.setVisibility(View.VISIBLE);
                { // ボタン1のアニメーション
                    Animation anim = new TranslateAnimation(-buttonTakeAPicture.getWidth() * 2, 0,
                            0, 0);
                    anim.setDuration(DELAY_START_ANIMATION_2);
                    buttonTakeAPicture.startAnimation(anim);
                }
                { // ボトム2のアニメーション
                    Animation anim = new TranslateAnimation(buttonTakeAPicture.getWidth() * 2, 0,
                            0, 0);
                    anim.setDuration(DELAY_START_ANIMATION_2);
                    buttonPickAPictureFromGalrally.startAnimation(anim);
                }
                mHandler.sendEmptyMessageDelayed(MESSAGE_START_ANIMATION_3, DELAY_START_ANIMATION_3);
            } else if (msg.what == MESSAGE_START_ANIMATION_3) {
                resetCatRabbitLayout();
                imageRabbit.setVisibility(View.VISIBLE);
                imageCat.setVisibility(View.VISIBLE);
                { // ボタン1のアニメーション
                    Animation anim = new RotateAnimation(90, 0, 0, imageRabbit.getHeight());
                    anim.setDuration(DELAY_START_ANIMATION_3);
                    imageRabbit.startAnimation(anim);
                }
                { // ボトム2のアニメーション
                    Animation anim = new RotateAnimation(-90, 0, imageCat.getWidth(),
                            imageCat.getHeight());
                    anim.setDuration(DELAY_START_ANIMATION_3);
                    imageCat.startAnimation(anim);
                }
                // mHandler.sendEmptyMessageDelayed(MESSAGE_START_ANIMATION_4,
                // DELAY_START_ANIMATION_4);
            }
        };
    };

    @Override
    protected void onResume() {
        super.onResume();

        { // 一回全部INVISIBLEにする
            View imageTop = findViewById(R.id.image_top);
            View imageBottom = findViewById(R.id.image_bottom);
            View buttonTakeAPicture = findViewById(R.id.button_taka_a_picture);
            View buttonPickAPictureFromGalrally = findViewById(R.id.button_pick_a_picture);
            View imageRabbit = findViewById(R.id.image_rabbit);
            View imageCat = findViewById(R.id.image_cat);

            imageTop.setVisibility(View.INVISIBLE);
            imageBottom.setVisibility(View.INVISIBLE);
            buttonTakeAPicture.setVisibility(View.INVISIBLE);
            buttonPickAPictureFromGalrally.setVisibility(View.INVISIBLE);
            imageRabbit.setVisibility(View.INVISIBLE);
            imageCat.setVisibility(View.INVISIBLE);
            mHandler.sendEmptyMessage(MESSAGE_START_ANIMATION_0);
        }
    }

    private void resetCatRabbitLayout() {
        Drawable bg = getResources().getDrawable(R.drawable.bg_bottom);
        Drawable rabbit = getResources().getDrawable(R.drawable.bg_rabbit);
        Drawable cat = getResources().getDrawable(R.drawable.bg_cat);

        View imageRabbit = findViewById(R.id.image_rabbit);
        View imageCat = findViewById(R.id.image_cat);
        View imageBottom = findViewById(R.id.image_bottom);
        float scale = (float)imageBottom.getWidth() / (float)bg.getMinimumWidth();

        int topOffset = -imageBottom.getHeight() / 4;
        int rabittPos = (int)(imageBottom.getWidth() * scale * 0.30);
        int catPos = (int)(imageBottom.getWidth() * scale * 0.78);
        imageBottom.bringToFront();
        imageRabbit.layout(//
                rabittPos, // Left
                topOffset + (int)(imageBottom.getTop()), // Top
                rabittPos + (int)(rabbit.getMinimumWidth() * scale), // Right
                topOffset + (int)(imageBottom.getTop() + rabbit.getMinimumHeight() * scale) // Bottom
        );
        imageCat.layout(//
                catPos, // // Left
                topOffset + (int)(imageBottom.getTop()), // Top
                catPos + (int)(cat.getMinimumWidth() * scale), // Right
                topOffset + (int)(imageBottom.getTop() + cat.getMinimumHeight() * scale) // Bottom
        );
    }
}
