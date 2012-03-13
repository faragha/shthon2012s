
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.eaglesakura.lib.android.game.thread.UIHandler;
import com.eaglesakura.lib.android.game.util.GameUtil;
import com.eaglesakura.lib.android.game.util.LogUtil;

/**
 * お絵かき用のActivity
 * 
 * @author TAKESHI YAMASHITA
 */
public class OekakiActivity extends BluetoothActivity {

    /**
     * 画像の保存先を伝える
     */
    public static final String INTENT_IMAGE_URI = "INTENT_IMAGE_URI";

    /**
     * bluetoothで受け取った画像のURI。 こっちに格納される場合がある。
     */
    public static final String INTENT_IMAGE_RESP = "INTENT_IMAGE_RESP";

    /**
     * キャッシュファイルの保存先
     */
    private static final File CACHE_FILE = new File(Environment.getExternalStorageDirectory(),
            ".pants.cache");

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
     * フレーム用ダイアログ
     */
    AlertDialog mFrameDialog;

    /**
     * 美白用ダイアログ
     */
    AlertDialog mWhiteningDialog;

    /**
     * 色リソース配列
     */
    private Integer[] mColorResources = {
            R.drawable.color_yellow, R.drawable.color_orange, R.drawable.color_green,
            R.drawable.color_black, R.drawable.color_blue, R.drawable.color_pink,
            R.drawable.color_red, R.drawable.color_white,
    };

    /**
     * 太さリソース配列
     */
    private Integer[] mNibResources = {
            R.drawable.bold01, R.drawable.bold02, R.drawable.bold03, R.drawable.bold04,
    };

    /**
     * スタンプリソース配列
     */
    private Integer[] mStampResources = {
            R.drawable.ribon, R.drawable.kirakira, R.drawable.flower, R.drawable.cat,
            R.drawable.rose, R.drawable.hell, R.drawable.king, R.drawable.hart,
    };

    /**
     * フレームリソース配列
     */
    private Integer[] mFrameResources = {
            R.drawable.frame01, R.drawable.frame02, R.drawable.frame03,
    };

    /**
     * 選択されている色配列Index
     */
    Integer mSelectedColor = 7;

    /**
     * 選択されている太さ配列Index
     */
    Integer mSelectedNib = 0;

    /**
     * 選択されているスタンプ配列Index
     */
    Integer mSelectedStamp = 0;

    /**
     * 選択されているフレーム配列Index
     */
    Integer mSelectedFrame = 0;

    /**
     * 色リソースマッピング用
     */
    Map<Integer, Integer> mColorMap = new HashMap<Integer, Integer>();

    Map<Integer, Float> mNibMap = new HashMap<Integer, Float>();

    Map<Integer, String> mStampMap = new HashMap<Integer, String>();

    Map<Integer, String> mFrameMap = new HashMap<Integer, String>();

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

            findViewById(R.id.oekaki_frame_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFrameDialog.show();
                }
            });

            findViewById(R.id.oekaki_white_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWhiteningDialog.show();
                }
            });

            findViewById(R.id.oekaki_finish_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new Builder(OekakiActivity.this);
                    builder.setMessage("画像を保存して終了しますか？")
                            .setPositiveButton("保存", new DialogInterface.OnClickListener() {
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
     * 画像のキャプチャを行う。 処理自体は非同期で裏で行われるから、終わったらdismissの必要がある。
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
                        intent.putExtra(CompleteActivity.INTENT_IMAGE_URI, Uri.fromFile(path)
                                .toString());
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
     * 
     * @return
     */
    private boolean isImageOwner() {
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
    protected void onMessageRead(final String message) {
        // TODO 怪しいから別スレッドに動かす。
        // 怪しいから怪しい挙動したらここ削除してUIスレッドで直実行してね
        if (GameUtil.isUIThread()) {
            (new Thread() {
                public void run() {
                    onMessageRead(message);
                }
            }).start();
            return;
        }

        // 画像を送ってくれっていうメッセージを受けた
        if (MESSAGE_REQUEST_IMAGE.equals(message)) {
            if (!isImageOwner()) {
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
        mColorMap.put(R.drawable.color_yellow, Color.rgb(229, 235, 63));
        mColorMap.put(R.drawable.color_orange, Color.rgb(235, 171, 61));
        mColorMap.put(R.drawable.color_green, Color.rgb(107, 235, 62));
        mColorMap.put(R.drawable.color_black, Color.rgb(0, 0, 0));
        mColorMap.put(R.drawable.color_blue, Color.rgb(62, 112, 235));
        mColorMap.put(R.drawable.color_pink, Color.rgb(235, 61, 146));
        mColorMap.put(R.drawable.color_red, Color.rgb(235, 62, 64));
        mColorMap.put(R.drawable.color_white, Color.rgb(255, 255, 255));

        mNibMap.put(R.drawable.bold01, 4.0f);
        mNibMap.put(R.drawable.bold02, 8.0f);
        mNibMap.put(R.drawable.bold03, 12.0f);
        mNibMap.put(R.drawable.bold04, 16.0f);

        mStampMap.put(R.drawable.ribon, "ribon_nodpi");
        mStampMap.put(R.drawable.kirakira, "kirakira_nodpi");
        mStampMap.put(R.drawable.flower, "flower_nodpi");
        mStampMap.put(R.drawable.cat, "cat_nodpi");
        mStampMap.put(R.drawable.rose, "rose_nodpi");
        mStampMap.put(R.drawable.hell, "hell_nodpi");
        mStampMap.put(R.drawable.king, "king_nodpi");
        mStampMap.put(R.drawable.hart, "hart_nodpi");

        mFrameMap.put(R.drawable.frame01, "frame01_nodpi");
        mFrameMap.put(R.drawable.frame02, "frame02_nodpi");
        mFrameMap.put(R.drawable.frame03, "frame03_nodpi");
    }

    private void makeDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // 以下、ペン用ダイアログ
        View layout = inflater.inflate(R.layout.pen_dialog,
                (ViewGroup) findViewById(R.id.layout_root));

        GridView colorGrid = (GridView) layout.findViewById(R.id.color_grid);
        GridView nibGrid = (GridView) layout.findViewById(R.id.nib_grid);

        layout.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPenDialog.dismiss();
                int colorCode = mColorMap.get(mColorResources[mSelectedColor]);
                float nibSize = mNibMap.get(mNibResources[mSelectedNib]);
                Pen pen = new Pen();
                pen.setTegakiData(nibSize, Color.red(colorCode), Color.green(colorCode),
                        Color.blue(colorCode));
                render.getDocument().setPen(pen);
            }
        });

        colorGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                parent.getChildAt(mSelectedColor).setBackgroundColor(Color.TRANSPARENT);
                v.setBackgroundColor(Color.rgb(235, 204, 213));
                mSelectedColor = position;
            }
        });

        nibGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                parent.getChildAt(mSelectedNib).setBackgroundColor(Color.TRANSPARENT);
                v.setBackgroundColor(Color.rgb(235, 204, 213));
                mSelectedNib = position;
            }
        });

        ImageAdapter colorAdapter = new ImageAdapter(mColorResources, mSelectedColor, true);
        colorGrid.setAdapter(colorAdapter);

        ImageAdapter nibAdapter = new ImageAdapter(mNibResources, mSelectedNib, true);
        nibGrid.setAdapter(nibAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        mPenDialog = builder.create();

        // 以下、スタンプ用ダイアログ
        layout = inflater
                .inflate(R.layout.stamp_dialog, (ViewGroup) findViewById(R.id.layout_root));
        GridView stampGrid = (GridView) layout.findViewById(R.id.stamp_grid);

        stampGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mStampDialog.dismiss();
                mSelectedStamp = position;

                String stampName = mStampMap.get(mStampResources[mSelectedStamp]);
                Pen pen = new Pen();
                pen.setStampData(stampName);
                render.getDocument().setPen(pen);
            }
        });

        ImageAdapter stampAdapter = new ImageAdapter(mStampResources, mSelectedStamp, false);
        stampGrid.setAdapter(stampAdapter);
        builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        mStampDialog = builder.create();

        // 以下、フレーム用ダイアログ
        layout = inflater
                .inflate(R.layout.frame_dialog, (ViewGroup) findViewById(R.id.layout_root));
        GridView frameGrid = (GridView) layout.findViewById(R.id.frame_grid);

        frameGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mFrameDialog.dismiss();
                mSelectedFrame = position;

                String frameName = mFrameMap.get(mFrameResources[mSelectedFrame]);
                Pen pen = new Pen();
                pen.setFrameData(frameName);
                render.getDocument().setPen(pen);
            }
        });

        ImageAdapter frameAdapter = new ImageAdapter(mFrameResources, mSelectedFrame, false);
        frameGrid.setAdapter(frameAdapter);
        builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        mFrameDialog = builder.create();

        // 以下、美白用ダイアログ
        layout = inflater.inflate(R.layout.whitening_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // ! TODO ここに美白設定を追加
                {
                    Pen pen = new Pen();
                    pen.setBihakuData(seekBar.getProgress());
                    render.getDocument().setPen(pen);
                    render.rendering();
                }
            }
        });
        builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        mWhiteningDialog = builder.create();
    }

    class ImageAdapter extends BaseAdapter {
        private Integer[] mIdList;

        private Integer mSelected;

        private boolean mIsBg;

        public ImageAdapter(Integer[] idList, Integer selected, boolean isBg) {
            mIdList = idList;
            mSelected = selected;
            mIsBg = isBg;
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
                imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mIdList[position]);

            if (mIsBg && mSelected == position) {
                imageView.setBackgroundColor(Color.rgb(235, 204, 213));
            } else {
                imageView.setBackgroundColor(Color.TRANSPARENT);
            }

            return imageView;
        }
    }

    private boolean sendedRequestImage = false;

    /**
     * 
     */
    @Override
    protected synchronized void onBluetoothConnectComplete() {
        super.onBluetoothConnectComplete();
        // 制御メッセージを送付済みだったら
        if (!sendedRequestImage) {
            if (!isImageOwner()) {
                sendedRequestImage = true;
                sendToOtherDevice(MESSAGE_REQUEST_IMAGE);
            }
        }
    }
}
