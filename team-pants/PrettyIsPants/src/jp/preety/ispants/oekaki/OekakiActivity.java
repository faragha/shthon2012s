package jp.preety.ispants.oekaki;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.preety.ispants.CompleteActivity;
import jp.preety.ispants.R;
import jp.preety.ispants.bluetooth.BluetoothActivity;
import jp.preety.ispants.oekaki.data.Data;
import jp.preety.ispants.oekaki.data.Pen;
import net.arnx.jsonic.JSON;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.eaglesakura.lib.android.game.thread.UIHandler;
import com.eaglesakura.lib.android.game.util.GameUtil;
import com.eaglesakura.lib.android.game.util.LogUtil;

/**
 * お絵かき用のActivity
 * @author TAKESHI YAMASHITA
 *
 */
public class OekakiActivity extends BluetoothActivity {

    /**
     * 画像の保存先を伝える
     */
    public static final String INTENT_IMAGE_URI = "INTENT_IMAGE_URI";

    /**
     * bluetoothで受け取った画像のURI。
     * こっちに格納される場合がある。
     */
    public static final String INTENT_IMAGE_RESP = "INTENT_IMAGE_RESP";

    /**
     * キャッシュファイルの保存先
     */
    private static final File CACHE_FILE = new File(Environment.getExternalStorageDirectory(), ".pants.cache");

    /**
     * 他の端末から画像ポストのメッセージが来た。
     */
    public static final String MESSAGE_REQUEST_IMAGE = "MESSAGE_REQUEST_IMAGE";

    /**
     * お絵かき用のレンダリングオブジェクト
     */
    OekakiRender render = null;

    /**
     * ペン用ダイアログ
     */
    AlertDialog mPenDialog;
    /**
     * スタンプ用ダイアログ
     */
    AlertDialog mStampDialog;

    /**
     * 色リソース配列
     */
    private Integer[] mColorResources = {
            R.drawable.color_1, R.drawable.color_2, R.drawable.color_3, R.drawable.color_4, R.drawable.color_5,
            R.drawable.color_6, R.drawable.color_7, R.drawable.color_8,
    };

    /**
     * 太さリソース配列
     */
    private Integer[] mNibResources = {
            R.drawable.nib_1, R.drawable.nib_2, R.drawable.nib_3, R.drawable.nib_4,
    };

    /**
     * スタンプリソース配列
     */
    private Integer[] mStampResources = {
            R.drawable.stamp_1, R.drawable.stamp_2, R.drawable.stamp_3, R.drawable.stamp_4, R.drawable.stamp_5,
            R.drawable.stamp_6, R.drawable.stamp_7, R.drawable.stamp_8,
    };

    /**
     * 選択されている色配列Index
     */
    Integer mSelectedColor = 0;
    /**
     * 選択されている太さ配列Index
     */
    Integer mSelectedNib = 0;
    /**
     * 選択されているスタンプ配列Index
     */
    Integer mSelectedStamp = 0;

    /**
     * 色リソースマッピング用
     */
    Map<Integer, Integer> mColorMap = new HashMap<Integer, Integer>();
    Map<Integer, Float> mNibMap = new HashMap<Integer, Float>();
    Map<Integer, String> mStampMap = new HashMap<Integer, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oekaki);
        init();
        makeDialog();

        {
            findViewById(R.id.oekaki_pen_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPenDialog.show();
                }
            });

            findViewById(R.id.oekaki_stamp_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStampDialog.show();
                }
            });

            findViewById(R.id.oekaki_finish_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new Builder(OekakiActivity.this);
                    builder.setMessage("画像を保存して終了しますか？").setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startCapture();
                        }
                    }).show();
                }
            });
        }
        LogUtil.setOutput(true);
        render = new OekakiRender(this);
    }

    /**
     * 画像のキャプチャを行う。
     * 処理自体は非同期で裏で行われるから、終わったらdismissの必要がある。
     */
    public void startCapture() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("画像を保存中です");
        dialog.show();
        render.getRenderHandler().post(new Runnable() {
            @Override
            public void run() {
                final File path = new File(Environment.getExternalStorageDirectory(), "image"
                        + System.currentTimeMillis() + ".png");
                render.capture(path);
                (new UIHandler()).post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Intent intent = new Intent(OekakiActivity.this, CompleteActivity.class);
                        intent.putExtra(CompleteActivity.INTENT_IMAGE_URI, Uri.fromFile(path).toString());
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        render.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        render.onResume();
    }

    /**
     * サーバー
     * @return
     */
    private boolean isImageOrner() {
        return getIntent().getStringExtra(INTENT_IMAGE_URI) != null;
    }

    String createImageJSON(String uri) {
        Data data = new Data();
        try {
            InputStream is = getContentResolver().openInputStream(Uri.parse(uri));
            data.image = GameUtil.toByteArray(is);
            return JSON.encode(data);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 他の端末からデータを受け取った
     */
    @Override
    protected void onMessageRead(String message) {
        // 画像を送ってくれっていうメッセージを受けた
        if (MESSAGE_REQUEST_IMAGE.equals(message)) {
            if (!isImageOrner()) {
                // 画像所有者じゃなければ何もしない
                return;
            }
            sendToOtherDevice(createImageJSON(getIntent().getStringExtra(INTENT_IMAGE_URI)));
            return;
        }

        Data data = Data.fromRececiveData(message);
        if (data.image == null) {
            render.getDocument().getServer().add(data, message);
        } else {

            // 既に画像を受け取り済みだったら何もしない
            if (getIntent().getStringExtra(INTENT_IMAGE_RESP) != null) {
                return;
            }

            final byte[] bytes = data.image;
            try {
                FileOutputStream os = new FileOutputStream(CACHE_FILE);
                os.write(bytes);
                os.close();

                getIntent().putExtra(INTENT_IMAGE_RESP, Uri.fromFile(CACHE_FILE).toString());

            } catch (Exception e) {
                LogUtil.log(e);
                throw new RuntimeException("not image");
            }
        }
    }

    @Override
    protected void sendToOtherDevice(String sendData) {
        super.sendToOtherDevice(sendData);
    }

    private void init() {
        mColorMap.put(R.drawable.color_1, Color.rgb(255, 0, 0));
        mColorMap.put(R.drawable.color_2, Color.rgb(0, 255, 0));
        mColorMap.put(R.drawable.color_3, Color.rgb(0, 0, 255));
        mColorMap.put(R.drawable.color_4, Color.rgb(255, 255, 0));
        mColorMap.put(R.drawable.color_5, Color.rgb(255, 0, 255));
        mColorMap.put(R.drawable.color_6, Color.rgb(0, 255, 255));
        mColorMap.put(R.drawable.color_7, Color.rgb(128, 0, 0));
        mColorMap.put(R.drawable.color_8, Color.rgb(0, 128, 0));

        mNibMap.put(R.drawable.nib_1, 4.0f);
        mNibMap.put(R.drawable.nib_2, 8.0f);
        mNibMap.put(R.drawable.nib_3, 16.0f);
        mNibMap.put(R.drawable.nib_4, 32.0f);

        mStampMap.put(R.drawable.stamp_1, "stamp_1");
        mStampMap.put(R.drawable.stamp_2, "stamp_2");
        mStampMap.put(R.drawable.stamp_3, "stamp_3");
        mStampMap.put(R.drawable.stamp_4, "stamp_4");
        mStampMap.put(R.drawable.stamp_5, "stamp_5");
        mStampMap.put(R.drawable.stamp_6, "stamp_6");
        mStampMap.put(R.drawable.stamp_7, "stamp_7");
        mStampMap.put(R.drawable.stamp_8, "stamp_8");
    }

    private void makeDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // 以下、ペン用ダイアログ
        View layout = inflater.inflate(R.layout.pen_dialog, (ViewGroup) findViewById(R.id.layout_root));

        GridView colorGrid = (GridView) layout.findViewById(R.id.color_grid);
        GridView nibGrid = (GridView) layout.findViewById(R.id.nib_grid);

        layout.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPenDialog.dismiss();
                int colorCode = mColorMap.get(mColorResources[mSelectedColor]);
                float nibSize = mNibMap.get(mNibResources[mSelectedNib]);
                Pen pen = new Pen();
                pen.setTegakiData(nibSize, Color.red(colorCode), Color.green(colorCode), Color.blue(colorCode));
                render.getDocument().setPen(pen);
            }
        });

        colorGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                parent.getChildAt(mSelectedColor).setBackgroundColor(Color.TRANSPARENT);
                v.setBackgroundColor(Color.GRAY);
                mSelectedColor = position;
            }
        });

        nibGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                parent.getChildAt(mSelectedNib).setBackgroundColor(Color.TRANSPARENT);
                v.setBackgroundColor(Color.GRAY);
                mSelectedNib = position;
            }
        });

        ImageAdapter colorAdapter = new ImageAdapter(mColorResources, mSelectedColor);
        colorGrid.setAdapter(colorAdapter);

        ImageAdapter nibAdapter = new ImageAdapter(mNibResources, mSelectedNib);
        nibGrid.setAdapter(nibAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        mPenDialog = builder.create();

        // 以下、スタンプ用ダイアログ
        layout = inflater.inflate(R.layout.stamp_dialog, (ViewGroup) findViewById(R.id.layout_root));
        GridView stampGrid = (GridView) layout.findViewById(R.id.stamp_grid);

        stampGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mStampDialog.dismiss();
                parent.getChildAt(mSelectedStamp).setBackgroundColor(Color.TRANSPARENT);
                v.setBackgroundColor(Color.GRAY);
                mSelectedStamp = position;

                String stampName = mStampMap.get(mStampResources[mSelectedStamp]);
                Pen pen = new Pen();
                pen.setStampData(stampName);
                render.getDocument().setPen(pen);
            }
        });

        ImageAdapter stampAdapter = new ImageAdapter(mStampResources, mSelectedStamp);
        stampGrid.setAdapter(stampAdapter);
        builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        mStampDialog = builder.create();
    }

    class ImageAdapter extends BaseAdapter {
        private Integer[] mIdList;
        private Integer mSelected;

        public ImageAdapter(Integer[] idList, Integer selected) {
            mIdList = idList;
            mSelected = selected;
        }

        @Override
        public int getCount() {
            return mIdList.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(OekakiActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mIdList[position]);

            if (mSelected == position) {
                imageView.setBackgroundColor(Color.GRAY);
            } else {
                imageView.setBackgroundColor(Color.TRANSPARENT);
            }

            return imageView;
        }
    }

    /**
     * 
     */
    @Override
    protected void onBluetoothConnectComplete() {
        if (!isImageOrner()) {
            sendToOtherDevice(MESSAGE_REQUEST_IMAGE);
        }
    }
}
