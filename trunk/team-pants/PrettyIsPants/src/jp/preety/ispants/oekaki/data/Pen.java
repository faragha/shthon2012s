package jp.preety.ispants.oekaki.data;

import android.content.Context;
import android.content.res.Resources;

import com.eaglesakura.lib.android.game.graphics.Color;

/**
 * 
 * @author TAKESHI YAMASHITA
 *
 */
public class Pen {

    /**
     * 
     * @author TAKESHI YAMASHITA
     *
     */
    public enum Type {

        /**
         * 手書き
         */
        Tegaki,

        /**
         * スタンプ
         */
        Stamp,

        /**
         * フォトフレーム
         */
        Frame,

        /**
         * 美白レベル
         */
        Bihaku
    }

    /**
     * 描画の種類設定
     */
    public Type type = Type.Tegaki;

    /**
     * ペンの幅
     */
    public float width = 5;

    /**
     * ペンの色
     */
    public Color color = new Color();

    /**
     * スタンプの名前
     */
    public String stampName = null;

    /**
     * フレームの名前
     */
    public String frameName = null;

    /**
     * 美白レベル
     */
    public float bihakuLevel = 0.0f;

    /**
     * 
     */
    public Pen() {

    }

    /**
     * 手書きデータを指定する
     * @param width ペン幅
     * @param r 色R[0-255]
     * @param g 色G[0-255]
     * @param b 色B[0-255]
     */
    public void setTegakiData(float width, int r, int g, int b) {
        this.width = width;
        color.set(r, g, b, 255);
        type = Type.Tegaki;
    }

    /**
     * スタンプデータを設定する
     * @param stampName
     */
    public void setStampData(String stampName) {
        this.stampName = stampName;
        type = Type.Stamp;
    }

    /**
     * スタンプのリソースIDを返す
     * @return スタンプのリソースID
     */
    public int getStampResId(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(stampName,"drawable",context.getPackageName());
        return resId;
    }

    /**
     * フォトフレームデータを設定する。
     * @param frameName
     */
    public void setFrameData(String frameName) {
        this.frameName = frameName;
        type = Type.Frame;
    }

    /**
     * フォトフレームのリソースIDを返す
     * @return スタンプのリソースID
     */
    public int getFrameResId(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(frameName,"drawable",context.getPackageName());
        return resId;
    }

    /**
     * 美白データを設定する。
     * @param bihakuLevel
     */
    public void setBihakuData(float bihakuLevel) {
        this.bihakuLevel = bihakuLevel;
        type = Type.Bihaku;
    }
}
