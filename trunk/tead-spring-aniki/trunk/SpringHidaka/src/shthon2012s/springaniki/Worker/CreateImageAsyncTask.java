
package shthon2012s.springaniki.Worker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import shthon2012s.springaniki.Utils.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

public class CreateImageAsyncTask extends AsyncTask<String, String, Boolean> {

    //private final String fromApp = "&form=App";

    Context parent;

    private CreateImageAsyncTaskCallback callback;

    private ProgressDialog progressDialog;

    private String filepath;

    /**
     * 画像のダウンロードと合成をバックグラウンドで行う
     *
     * @param context プログレスバーを出すため。Activityを渡してください
     * @param callback 完了通知用コールバックを登録してください。作成成功/失敗を通知します
     */
    public CreateImageAsyncTask(Context context, CreateImageAsyncTaskCallback callback) {
        this.callback = callback;

        // プログレスを作成する
        progressDialog = new ProgressDialog(context);
        parent = context;

        // TODO:速度が遅いからとりあえず出した。ほかの絵を用意する必要があるかも。
        progressDialog.setMessage("写真を受信中...");// context.getText(R.string.dialog_message_downloading));
    }

    public void onPreExecute() {
        // プログレスを表示する
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... url) {

        // ここでダウンロードする
        String accessUrl = url[0];

        int responseCode = 0;
        final int BUFFER_SIZE = 1024 * 50;

        try {
            URI uri = new URI(accessUrl);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.JAPAN);
            String fileName = (sdf.format(new Date())) + ".jpg";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse httpResponse = null;

            httpClient.getParams().setParameter("http.connection.timeout", new Integer(15000));
            httpResponse = httpClient.execute(httpGet);
            responseCode = httpResponse.getStatusLine().getStatusCode();

            // プログレスダイアログに進捗を設定(初期値)
            // progressDialog.setMax((int)
            // httpResponse.getEntity().getContentLength());

            if (responseCode == HttpStatus.SC_OK) {

                // データが存在したらダウンロードのためいったん保存する
                String dir = Utils.getAnikiDir();
                new File(dir).mkdir();

                File file = new File(dir, fileName);
                filepath = dir + "/" + fileName;

                InputStream inputStream = httpResponse.getEntity().getContent();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream,
                        BUFFER_SIZE);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                        new FileOutputStream(file, false), BUFFER_SIZE);

                byte buffer[] = new byte[BUFFER_SIZE];
                int size = 0;
                while (-1 != (size = bufferedInputStream.read(buffer))) {
                    bufferedOutputStream.write(buffer, 0, size);
                }

                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                bufferedInputStream.close();

            } else if (responseCode == HttpStatus.SC_NOT_FOUND) {
                return false;
            } else if (responseCode == HttpStatus.SC_REQUEST_TIMEOUT) {
                return false;
            } else {
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onPostExecute(Boolean result) {
        // プログレスを閉じる
        progressDialog.dismiss();

        if (result != true) {
            // エラーをコールバックで返す
            callback.onFailedCreateImage(-1);// TODO:手抜きを直すorz
        } else {
            // 保存先を通知
            callback.onSuccessCreateImage(filepath);
        }
    }
}
