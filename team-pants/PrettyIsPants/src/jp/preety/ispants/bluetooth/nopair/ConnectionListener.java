
package jp.preety.ispants.bluetooth.nopair;

/**
 * Bluetooth接続の状態を受け取るリスナー
 * 
 * @param <T>
 */
public interface ConnectionListener<T> {

    /**
     * 接続が切断したときに通知するためのコールバックメソッド
     * 
     * @param conn
     */
    void onConnectionClosed(T conn);

    /**
     * 接続が確立したときに通知するためのコールバックメソッド
     * 
     * @param conn
     */
    void onConnectionEstablished(T conn, boolean isServer);

    /**
     * 接続が失敗したときに通知するためのコールバックメソッド
     * 
     * @param conn
     */
    void onConnectionFailure(T conn);
}
