
package jp.sonicstudio.sutami.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import jp.sonicstudio.sutami.R;
import jp.sonicstudio.sutami.image.PreviewView;
import jp.sonicstudio.sutami.image.StampMaker;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * <p>
 * インテントで指定されたUriの画像を自動的にスタンプ画像に変換し、画像表示するクラス。<br>
 * 画像の保存、共有もできる。
 * </p>
 */
public class PreviewActivity extends Activity {

    /**
     * スタンプ画像に変換したい画像のUriを指定する（型はUri）。
     */
    public static final String IMAGE_URI = "image_uri";

    /** デバッグ用 */
    private static final String TAG = "PreviewActivity";

    /** MediaScanner 対象外にするためのファイル名 */
    private static final String NOMEDIA_FILE_NAME = ".nomedia";
    /** テンポラリ画像ファイル名 */
    private static final String TMP_IMAGE_FILE_NAME = "stamp.png";

    /** スタンプ画像最大幅 */
    private static final int MAX_WIDTH = 320;
    /** スタンプ画像最大高さ */
    private static final int MAX_HEIGHT = 320;

    private static final int MAX_LEVEL = 4; // index0ベース
    private static final int DEFAULT_LEVEL = 2; // index0ベース

    /** プレビュービュー */
    private PreviewView mPreviewView;

    /** 入力画像（ただし、最大サイズを超えないサイズで縮小して読み込む） */
    private Bitmap mSrcBitmap;
    /** 出力画像（スタンプ画像はテンポラリ画像として保存する） */
    private Bitmap mDstBitmap;

    private int mThreshold = StampMaker.THRESHOLD_LEVEL_3;
    private EditText decoNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルバー右上の不確定プログレスバーを使用
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.preview);
        // プレビュービュー初期化
        mPreviewView = (PreviewView) findViewById(R.id.previewview);
        // シークバー初期化
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setMax(MAX_LEVEL);
        seekBar.setProgress(DEFAULT_LEVEL);
        seekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        // デコメ名エディットを初期化
        decoNameEditText = (EditText) findViewById(R.id.decoName);

        // 保存ボタン初期化
        Button buttonSave = (Button) findViewById(R.id.button_save);
        buttonSave.setOnClickListener(mSaveOnClickListener);
        // 共有ボタン初期化
        Button buttonShare = (Button) findViewById(R.id.button_share);
        buttonShare.setOnClickListener(mShareOnClickListener);
        // 呼び出し元から指定されたインテント情報を取得
        Intent intent = getIntent();
        Uri uri = (Uri) intent.getParcelableExtra(IMAGE_URI);
        if (uri != null) {
            // スタンプ画像作成（非同期処理）
            LoadImageTask loadImageTask = new LoadImageTask();
            loadImageTask.execute(uri);
        }
        Button buttonUp = (Button) findViewById(R.id.button_up);
        buttonUp.setOnClickListener(mUpOnClickListener);
   }

    /**
     * シークバー変更リスナー
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            switch (progress) {
                case 0:
                    mThreshold = StampMaker.THRESHOLD_LEVEL_1;
                    break;
                case 1:
                    mThreshold = StampMaker.THRESHOLD_LEVEL_2;
                    break;
                case 2:
                    mThreshold = StampMaker.THRESHOLD_LEVEL_3;
                    break;
                case 3:
                    mThreshold = StampMaker.THRESHOLD_LEVEL_4;
                    break;
                case 4:
                    mThreshold = StampMaker.THRESHOLD_LEVEL_5;
                    break;
                default:
                    mThreshold = StampMaker.DEFAULT_THRESHOLD;
            }
            updateView();
        }
    };

    /**
     * 戻るボタンクリックリスナー
     */
    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // アクティビティーをキャンセルで終了する
            setResult(RESULT_CANCELED);
            finish();
        }
    };

    /**
     * 保存ボタンクリックリスナー
     */
    private View.OnClickListener mSaveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 画像を保存する
            saveImage(false); // false:共有しない
			makeDecome();
		}

		/**
		 * デコメを作成して保存する。
		 */
		private void makeDecome() {
			String decomeName = "deco";
			SaveDecomeTask saveDecomeTask = new SaveDecomeTask(PreviewActivity.this, false);
			saveDecomeTask.setDecoName(decoNameEditText.getText().toString());
			//saveDecomeTask.execute(mDstBitmap);
			saveDecomeTask.execute(mPreviewView.getSelectImage());
		}
    };

    /**
     * 共有ボタンクリックリスナー
     */
    private View.OnClickListener mShareOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 画像を一時保存し、画像をインテントで共有する
            saveImage(true); // true:共有する
        }
    };

    /**
     * アップロードボタンクリックリスナー
     */
    private View.OnClickListener mUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 画像をアップロードする
			String decomeName = "deco";
        	UploadDecomeTask uploadDecomeTask = new UploadDecomeTask(PreviewActivity.this);
			uploadDecomeTask.setDecoName(decoNameEditText.getText().toString());
        	//uploadDecomeTask.execute(mDstBitmap);
        	uploadDecomeTask.execute(mPreviewView.getSelectImage());
        	
        }
    };

    /**
     * 画像保存
     * 
     * @param isShare 　画像保存後、共有するかどうか
     */
    private void saveImage(boolean isShare) {
        SaveImageTask saveImageTask = new SaveImageTask(PreviewActivity.this,
                isShare);
		saveImageTask.setDecoName(decoNameEditText.getText().toString());
        //saveImageTask.execute(mDstBitmap);
        saveImageTask.execute(mPreviewView.getSelectImage());
       
    }

    /**
     * <p>
     * このアプリケーションの外部ストレージ内での保存ルートパスを取得する。<br>
     * 例） /sdcard/jp.sonicstudio.sutami
     * </p>
     * 
     * @return このアプリケーションの外部ストレージ内での保存ルートパス文字列
     */
    private String getAppExternalStoragePath() {
        ApplicationInfo ai = getApplicationInfo();
        String path = Environment.getExternalStorageDirectory().getPath()
                .toString()
                + File.separator + ai.packageName;
        return path;
    }

    /**
     * <p>
     * テンポラリ画像パス。<br>
     * 例） /sdcard/jp.sonicstudio.sutami/tmp.png
     * </p>
     * 
     * @return　テンポラリ画像パス文字列。
     */
    private String getTemporallyImagePath() {
        String path = getAppExternalStoragePath();
        path += File.separator + TMP_IMAGE_FILE_NAME;
        return path;
    }

    /**
     * ビュー更新（）
     */
    private void updateView() {
        MakeStampImageTask makeStampImageTask = new MakeStampImageTask();
        makeStampImageTask.execute(mSrcBitmap);
    }

    /**
     * 画像読み込みクラス（非同期処理）
     */
    class LoadImageTask extends AsyncTask<Uri, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Uri... params) {
            boolean success = false;
            if (params.length > 0) {
                if (params[0] != null) {
                    if (params[0] instanceof Uri) {
                        try {
                            if (mSrcBitmap == null) {
                                Uri uri = (Uri) params[0];
                                // 回転状態を取得する
                                String tmpFileDir = "/mnt/sdcard/sutami/";
                                String tmpFilePath = tmpFileDir + "/__tmp__.jpg";
                                File file = new File(tmpFileDir);
                                if (!file.exists()) {
                                    file.mkdir();
                                }
                    			InputStream input = getContentResolver().openInputStream(uri);
                    			FileOutputStream fos = new FileOutputStream(tmpFilePath);
                    			BufferedOutputStream bos = new BufferedOutputStream(fos);
                    			byte[] buf = new byte[4096];
                    			int bytesRead = 0;
                    			while (0 < (bytesRead = input.read(buf))) {
                    				bos.write(buf, 0, bytesRead);
                    			}
                    			bos.flush();
                    			bos.close();
                    			fos.close();
                    			ExifInterface exif = new ExifInterface(tmpFilePath);
                    			int rotation = 0;
                    			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    			switch (orientation) {
                    			case 6:
                    				rotation = 90;
                    				break;
                    			}
                    			File tmpFile = new File(tmpFilePath);
                    			tmpFile.delete();
                                
                                // 画像のサイズを取得（実際には画像を展開しない）
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                InputStream inputStream = getContentResolver()
                                        .openInputStream(uri);
                                BitmapFactory.decodeStream(inputStream, null,
                                        options);
                                inputStream.close();
                                // 画像を縮小して展開する
                                int scaleWidth = 1;
                                int scaleHeight = 1;
                                int outWidth;
                                int outHeight;
                                if (rotation == 90) {
                                	outWidth = options.outHeight;
                                	outHeight = options.outWidth;
                                }
                                else {
                                	outWidth = options.outWidth;
                                	outHeight = options.outHeight;
                                }
                                if ((outWidth > MAX_WIDTH)
                                        || (outHeight > MAX_HEIGHT)) {
                                    scaleWidth = outWidth / MAX_WIDTH + 1;
                                    scaleHeight = outHeight / MAX_HEIGHT + 1;
                                }
                                int scale = Math.max(scaleWidth, scaleHeight);
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = scale;
                                inputStream = getContentResolver().openInputStream(
                                        uri);
                                Bitmap _mSrcBitmap = BitmapFactory.decodeStream(
                                        inputStream, null, options);
                                inputStream.close();
                				if (rotation != 0) {
                                    Matrix matrix = new Matrix();
                					matrix.postRotate(rotation);
                                    mSrcBitmap = Bitmap.createBitmap(_mSrcBitmap, 0, 0, _mSrcBitmap.getWidth(), _mSrcBitmap.getHeight(), matrix, true);
                				}
                				else {
                					mSrcBitmap = _mSrcBitmap;
                				}
                                ////
                                

                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            updateView();
        }

    }

    /**
     * スタンプ画像作成クラス（非同期処理）
     */
    class MakeStampImageTask extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            // タイトルバー右上の不確定プログレスバーを表示
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap bitmap = null;
            if (params.length > 0) {
                if (params[0] != null) {
                    if (params[0] instanceof Bitmap) {
                        Bitmap srcBitmap = (Bitmap) params[0];
                        // スタンプ画像作成処理を呼び出す
                        StampMaker stampMaker = new StampMaker();
                        stampMaker.initialize(srcBitmap);
                        bitmap = stampMaker.process(mThreshold); // 閾値
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            mDstBitmap = result;
            mPreviewView.setBitmap(mDstBitmap);
            // タイトルバー右上の不確定プログレスバーを非表示
            setProgressBarIndeterminateVisibility(false);
        }

    }

    /**
     * 画像保存クラス（非同期処理）
     */
    class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {

        private Context mContext;
        private boolean mIsShare;
        private ProgressDialog mProgressDialog;
        private String decoName;
		private final String STAMP_OUTPUT_DIR = "/mnt/sdcard/sutami";
		private String imageFilePath;

        /**
         * コンストラクタ
         * 
         * @param context コンテキスト
         * @param isShare 保存処理後、共有するかどうか
         */
        public SaveImageTask(Context context, boolean isShare) {
            mContext = context;
            mIsShare = isShare;
            imageFilePath = "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // プログレスダイアログ表示
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.progress_to_save_image));
            mProgressDialog.show();
        }

		public void setDecoName(String name) {
			decoName = name;
		}
		
        @Override
        protected Boolean doInBackground(Bitmap... params) {
            boolean success = false;
            if (params.length > 0) {
                if (params[0] != null) {
                    if (params[0] instanceof Bitmap) {
                    	imageFilePath = STAMP_OUTPUT_DIR + "/" + decoName + ".png";
                        Bitmap bitmap = params[0];
                        // このアプリケーションの外部ストレージ内での保存ルートパスのディレクトリが存在しなければ作成する
                        File file = new File(STAMP_OUTPUT_DIR);
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        // 指定された画像をテンポラリパスのファイルにPNG形式で保存する
                        File imageFile = new File(imageFilePath);
                        try {
                            FileOutputStream fos = new FileOutputStream(imageFile);
                            bitmap.compress(CompressFormat.PNG, 100, fos);
                            fos.close();
                            ContentResolver contentresolver = mContext.getContentResolver();
                            ContentValues contentvalues = new ContentValues();
                            contentvalues.put(Images.Media.MIME_TYPE, "image/png");
                            contentvalues.put(Images.Media.DATA, imageFile.getPath());
                            contentvalues.put(Images.Media.TITLE, imageFile.getName());
                            contentvalues.put(Images.Media.DISPLAY_NAME, decoName);
                            contentresolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentvalues);
                            success = true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // プログレスダイアログを閉じる
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (mIsShare) {
                // 共有する場合、失敗時のみトースト表示する
                if (result) {
                    shareImage();
                } else {
                    String text = mContext.getString(R.string.failed_to_share_image);
                    Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
                }
            } else {
                // 保存のみの場合、常にトースト表示する
                int resId;
                if (result) {
                    resId = R.string.success_to_save_image;
                } else {
                    resId = R.string.failed_to_save_image;
                }
                String text = mContext.getString(resId);
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * インテント経由でテンポラリ画像を共有する
         */
        private void shareImage() {
        	if (!imageFilePath.equals("")) {
	            Uri uri = Uri.fromFile(new File(imageFilePath));
	            Intent intent = new Intent(Intent.ACTION_SEND);
	            intent.setType("image/png");
	            intent.putExtra(Intent.EXTRA_STREAM, uri);
	            startActivity(Intent.createChooser(intent, null));
        	}
        }

    };

	/**
	 * デコメ保存クラス(画像保存クラスの丸パク)
	 */
	class SaveDecomeTask extends AsyncTask<Bitmap, Void, Boolean> {

		private Context mContext;
		private boolean mIsShare;
		private ProgressDialog mProgressDialog;
		private final String DECOME_OUTPUT_DIR = "/mnt/sdcard/download";
		private String decoName;

		/**
		 * コンストラクタ
		 * 
		 * @param context コンテキスト
		 * @param isShare 保存処理後、共有するかどうか
		 */
		public SaveDecomeTask(Context context, boolean isShare) {
			mContext = context;
			mIsShare = isShare;
		}

		public void setDecoName(String name) {
			decoName = name;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// プログレスダイアログ表示
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext.getString(R.string.progress_to_save_decome));
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Bitmap... params) {
			boolean success = false;
			if (params.length > 0) {
				if (params[0] != null) {
					if (params[0] instanceof Bitmap) {
						Bitmap bitmap = params[0];
						// このアプリケーションの外部ストレージ内での保存ルートパスのディレクトリが存在しなければ作成する
						File file = new File(DECOME_OUTPUT_DIR);
						if (!file.exists()) {
							file.mkdir();
						}

						// 指定された画像をJPEGで書きだす
						File imageFile = new File(DECOME_OUTPUT_DIR + "/" + decoName + ".jpg");
						try {
							Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
									bitmap.getConfig());
							Canvas canvas = new Canvas(outBitmap);
							canvas.drawColor(Color.WHITE);
							canvas.drawBitmap(bitmap, 0, 0, null);
							
							FileOutputStream fos = new FileOutputStream(imageFile);
							outBitmap.compress(CompressFormat.JPEG, 100, fos);
							fos.close();
							success = true;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return success;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// プログレスダイアログを閉じる
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			if (mIsShare) {
				// 共有する場合、失敗時のみトースト表示する
				if (result) {
					shareImage();
				} else {
					String text = mContext.getString(R.string.failed_to_share_decome);
					Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
				}
			} else {
				// 保存のみの場合、常にトースト表示する
				int resId;
				if (result) {
					resId = R.string.success_to_save_decome;
				} else {
					resId = R.string.failed_to_save_decome;
				}
				String text = mContext.getString(resId);
				Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
			}
		}

		/**
		 * インテント経由でテンポラリ画像を共有する
		 */
		private void shareImage() {
			Uri uri = Uri.fromFile(new File(getTemporallyImagePath()));
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			startActivity(Intent.createChooser(intent, null));
		}

	};

	/**
	 * デコメアップロードクラス(画像保存クラスの丸パク)
	 */
	class UploadDecomeTask extends AsyncTask<Bitmap, Void, Boolean> {

		private Context mContext;
		private ProgressDialog mProgressDialog;
		private final String DECOME_OUTPUT_DIR = "/mnt/sdcard/_tmp_decome";
		private String decoName;

		/**
		 * コンストラクタ
		 * 
		 * @param context コンテキスト
		 * @param isShare 保存処理後、共有するかどうか
		 */
		public UploadDecomeTask(Context context) {
			mContext = context;
		}

		public void setDecoName(String name) {
			decoName = name;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// プログレスダイアログ表示
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext.getString(R.string.progress_to_up_decome));
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Bitmap... params) {
			boolean success = false;
			if (params.length > 0) {
				if (params[0] != null) {
					if (params[0] instanceof Bitmap) {
						Bitmap bitmap = params[0];
						// このアプリケーションの外部ストレージ内での保存ルートパスのディレクトリが存在しなければ作成する
						File file = new File(DECOME_OUTPUT_DIR);
						if (!file.exists()) {
							file.mkdir();
						}

						// 指定された画像をJPEGで書きだす
                        String imageFilePath = DECOME_OUTPUT_DIR + "/" + decoName + ".png";
                        File imageFile = new File(imageFilePath);
                        try {
                            FileOutputStream fos = new FileOutputStream(imageFile);
                            bitmap.compress(CompressFormat.PNG, 100, fos);
                            fos.close();
                            ContentResolver contentresolver = mContext.getContentResolver();
                            ContentValues contentvalues = new ContentValues();
                            contentvalues.put(Images.Media.MIME_TYPE, "image/png");
                            contentvalues.put(Images.Media.DATA, imageFile.getPath());
                            contentvalues.put(Images.Media.TITLE, imageFile.getName());
                            contentvalues.put(Images.Media.DISPLAY_NAME, decoName);
                            contentresolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentvalues);
                            success = true;

							MultipartEntity entity = new MultipartEntity();
							FileBody fileBody = new FileBody(new File(imageFilePath));
							System.out.println("MIME type: " + fileBody.getMimeType());
							StringBody msgBody;
							msgBody = new StringBody(decoName, Charset.forName("UTF-8"));
							entity.addPart("file", fileBody);
							entity.addPart("title", msgBody);
							DefaultHttpClient client = new DefaultHttpClient();
							HttpPost httpPost = new HttpPost(getString(R.string.deco_upload_to));
							httpPost.setEntity(entity);
							HttpResponse resp = client.execute(httpPost);
							String respString = EntityUtils.toString(resp.getEntity(), "UTF-8");
							System.out.println("resp: " + respString);
							////
							success = true;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return success;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// プログレスダイアログを閉じる
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			// 保存のみの場合、常にトースト表示する
			int resId;
			if (result) {
				resId = R.string.success_to_up_decome;
			} else {
				resId = R.string.failed_to_up_decome;
			}
			String text = mContext.getString(resId);
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
	};
}
