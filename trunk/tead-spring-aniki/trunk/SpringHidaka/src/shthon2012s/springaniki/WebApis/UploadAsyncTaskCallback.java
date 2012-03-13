
package shthon2012s.springaniki.WebApis;

/**
 * Upload完了通知用のコールバックインターフェイス
 * 
 * @author mhidaka
 */
public interface UploadAsyncTaskCallback {

    /**
     * 画像のアップロードが成功した時に呼ばれるメソッド
     * 
     * @param url 画像のUpload先URL
     */
    void onSuccessUploadImage(String url);

    /**
     * 画像のアップロードが失敗した時に呼ばれるメソッド
     * 
     * @param statusCode エラー時のステータスコード (TODO:現状-1固定なのでネットワークがおかしいようです？とか出せば良いかも)
     */
    void onFailedUploadImage(int statusCode);
}
