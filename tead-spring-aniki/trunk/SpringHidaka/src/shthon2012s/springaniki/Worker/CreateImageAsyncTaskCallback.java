
package shthon2012s.springaniki.Worker;

/**
 * ダウンロード＆画像生成完了通知用のコールバックインターフェイス
 * 
 * @author mhidaka
 */
public interface CreateImageAsyncTaskCallback {

    /**
     * Webから画像を取得してフレーム合成が成功した時に呼ばれるメソッド
     * 
     * @param filepath 作成画像の保存先のファイルパス
     */
    void onSuccessCreateImage(String filepath);

    /**
     * 画像のアップロードが失敗した時に呼ばれるメソッド
     * 
     * @param statusCode エラー時のステータスコード (TODO:現状-1固定なのでネットワークがおかしいようです？とか出せば良いかも)
     */
    void onFailedCreateImage(int statusCode);
}
