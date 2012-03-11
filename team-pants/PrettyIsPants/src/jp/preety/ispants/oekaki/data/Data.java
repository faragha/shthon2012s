package jp.preety.ispants.oekaki.data;

import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;

import com.eaglesakura.lib.android.game.math.Vector2;

/**
 * 描画用データクラス。
 * データは基本的にJSON化を簡単にするため、全てpublicにする。
 * @author TAKESHI YAMASHITA
 *
 */
public class Data {

    /**
     * 書き物データ
     */
    public static final String TYPE_STROKE = "TYPE_STROKE";

    /**
     * 画像データ
     */
    public static final String TYPE_PHOTO = "TYPE_PHOTO";

    /**
     * 画像ファイルのbyte配列。
     * RGB配列はダメ絶対
     */
    public byte[] image = null;

    /**
     * データタイプ
     */
    public String type = null;

    /**
     * 描画用のペン
     */
    public Pen pen = null;

    /**
     * データを作成した時間。
     * 書き込んだ時間が若い順から描画する。
     */
    public long beginTime = System.currentTimeMillis();

    /**
     * 描画用の点リスト
     */
    public List<Vector2> touchPoints = new ArrayList<Vector2>();

    public Data() {

    }

    /**
     * bluetoothやり取り用
     * @param data
     * @return
     */
    public static String toSendingData(Data data) {
        //JSON 変換
        return JSON.encode(data);
    }

    /***
     * bluetoothからの読み取り用
     * @param data
     * @return
     */
    public static Data fromRececiveData(String data) {
        // JSONから変換
        return JSON.decode(data, Data.class);
    }
}
