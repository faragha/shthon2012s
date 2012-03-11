package jp.preety.ispants.oekaki.data;

import java.util.ArrayList;
import java.util.List;

import jp.preety.ispants.oekaki.OekakiRender;

/**
 * スタンプやら手描きやらのの情報を管理するサーバー
 * @author TAKESHI YAMASHITA
 *
 */
public class DataServer {
    List<OnDataUpdateListener> listeners = new ArrayList<DataServer.OnDataUpdateListener>();

    /**
     * 描画データ一覧。
     * このデータは全端末で共有される。
     */
    List<Data> datas = new ArrayList<Data>();

    OekakiRender render = null;

    /**
     * 
     */
    public DataServer(OekakiRender render) {
        this.render = render;
    }

    /**
     * サーバーにリスナを登録する。
     * @param listener
     */
    public void addListener(OnDataUpdateListener listener) {
        if (listeners.indexOf(listener) < 0) {
            listeners.add(listener);
        }
    }

    /**
     * サーバー上からリスナを削除する。
     * @param listener
     */
    public void removeListener(OnDataUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * データをローカルに追加する。
     * @param data
     */
    private void _add(Data data) {
        //        datas.add(data);
    }

    /**
     * 描画データを追加する。
     * @param dataJson
     */
    public void add(Data data, String dataJson) {
        _add(data);
        for (OnDataUpdateListener listener : listeners) {
            listener.onDataReceve(this, data);
        }
    }

    /**
     * 描画データを追加する。
     * @param data
     */
    public void add(Data data) {
        _add(data);
        String sendingData = Data.toSendingData(data);
        for (OnDataUpdateListener listener : listeners) {
            listener.onDataAdded(this, data, sendingData);
        }
    }

    /**
     * 
     * @author TAKESHI YAMASHITA
     *
     */
    public interface OnDataUpdateListener {
        /**
         * データを受け取ったら呼び出される
         * @param data
         */
        void onDataReceve(DataServer server, Data data);

        /**
         * ユーザーが書いてデータが登録されたら呼び出される。
         * @param server
         * @param data
         * @param json
         */
        void onDataAdded(DataServer server, Data data, String json);
    }
}
