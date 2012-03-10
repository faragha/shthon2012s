package shthon2012s.springhidaka.WebApis;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;

public class UploadAsyncTask extends AsyncTask<String, String, String> {

	private final String url = "https://shthon2012s.appspot.com/collection";

	private UploadAsyncTaskCallback callback;
	private ProgressDialog progressDialog;

	/**
	 * 画像のアップロードをバックグラウンドで行う
	 *
	 * @param context
	 *            プログレスバーを出すため。Activityを渡してください
	 * @param callback
	 *            完了通知用コールバックを登録してください。送信成功/失敗を通知します
	 */
	public UploadAsyncTask(Context context, UploadAsyncTaskCallback callback) {
		this.callback = callback;

		// プログレスを作成する
		progressDialog = new ProgressDialog(context);

		// TODO:速度が遅いからとりあえず出した。ほかの絵を用意する必要があるかも。
		progressDialog.setMessage("写真を送信中...");//context.getText(R.string.dialog_message_downloading));
	}

	public void onPreExecute() {
		// プログレスを表示する
		progressDialog.show();
	}

	@Override
	protected String doInBackground(String... filepath) {

		// ここでアップロードする
		String accessUrl = UploadFile(filepath[0]);

		return accessUrl;
	}

	public void onPostExecute(String result) {
		// プログレスを閉じる
		progressDialog.dismiss();
		if (result == null) {
			// エラーをコールバックで返す
			callback.onFailedUploadImage(-1);// TODO:手抜きを直すorz
		} else {
			// アップロードしたIDをコールバックでを返す(Getメソッドで呼ぶだけ)
			String downloadUrl = url + "?" + result;
			callback.onSuccessUploadImage(downloadUrl);
		}
	}

	//サーバーへのUpload用
	public String UploadFile(String filepath) {

		try {
			// POST リクエスト準備
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url); // 送信先URL（http://は入っていること）
			MultipartEntity entity = new MultipartEntity();
			HttpResponse res;

			// 画像は存在する時のみ送る
			File fd = new File(filepath);
			if (!fd.exists()) {

				return null;
			}

			// パラメータをセットする
			// entity.addPart("id", new StringBody(id)); // 文字列とかを送るときの書き方
			entity.addPart("imgfile", new FileBody(new File(filepath)));

			// パラメータの設定ここまで
			post.setEntity(entity);

			// リクエストしサーバステータスを獲得
			res = client.execute(post);

			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// ステータスがOKの時の返答を参照。XMLの解析
				InputStream in = res.getEntity().getContent();
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(new InputStreamReader(in));
				String parserName;

				for (int e = parser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = parser
						.next()) {
					switch (e) {
					case XmlPullParser.START_TAG:
						parserName = parser.getName();
						if (parserName.equals("result")) {
							// result値（サーバの処理結果）を獲得
							// サーバからのメッセージを獲得し、Stringにセット
							return parser.getAttributeValue(null, "result");
						}
						break;
					case XmlPullParser.TEXT:
						break;
					case XmlPullParser.END_TAG:
						break;
					}
				}
			} else {
			}
		} catch (Exception e) {
		}
		// 結果を返答
		return null;
	}
}
