package jp.preety.ispants.oekaki.render;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import jp.preety.ispants.oekaki.OekakiRender;
import jp.preety.ispants.oekaki.data.Data;

import com.eaglesakura.lib.android.game.graphics.DisposableResource;
import com.eaglesakura.lib.android.game.math.Vector2;

/**
 * 
 * @author TAKESHI YAMASHITA
 *
 */
public abstract class TegakiStackBase extends DisposableResource {

    protected Data data = new Data();
    protected OekakiRender render;

    /**
     * データを新規作成する。
     * @param render
     */
    protected TegakiStackBase(OekakiRender render) {
        this.render = render;
        data.pen = render.getDocument().getPen();
        realloc();
    }

    /**
     * データを復元する。
     * @param render
     * @param data
     */
    protected TegakiStackBase(OekakiRender render, Data _data) {
        this(render);
        this.data.pen = _data.pen;
        //! データをコピーする
        for (Vector2 v : _data.touchPoints) {
            put(v.x, v.y);
        }
    }

    @Override
    public void dispose() {

    }

    /**
     * このオブジェクトを描画する。
     */
    public abstract void draw();

    /**
     * 頂点バッファ
     */
    private FloatBuffer buffer = null;

    /**
     * 描画用のポインタ
     */
    private int pointer = 0;

    /**
     * ライン用の位置を書き込む
     * @param u
     * @param v
     */
    public void put(final float u, final float v) {
        if (pointer == buffer.capacity()) {
            realloc();
        }
        // データを更新する
        data.touchPoints.add(new Vector2(u, v));
        data.beginTime = System.currentTimeMillis();

        final float x = u * 2 - 1.0f;
        final float y = u * 2 - 1.0f;
        buffer.put(pointer++, x);
        buffer.put(pointer++, y);
        buffer.position(0);
    }

    /**
     * 共有用のデータを取得する。
     * @return
     */
    public Data getData() {
        return data;
    }

    /**
     * 描画ようのバッファを取得する。
     * @return
     */
    public Buffer getBuffer() {
        return buffer;
    }

    /**
     * 描画ようのバッファを取得する。
     * @return
     */
    public FloatBuffer getRawBuffer() {
        return buffer;
    }

    /**
     * 登録済みのポイント数を取得する。
     * @return
     */
    public int getPointNum() {
        return pointer / 2;
    }

    /**
     * メモリを再度確保する。
     */
    private void realloc() {
        if (buffer == null) {
            final int DEFAULT_LENGTH = 2 * 4 * 100; //!< 100地点くらいあれば大丈夫かな？
            buffer = ByteBuffer.allocateDirect(DEFAULT_LENGTH * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        } else {
            final FloatBuffer next = ByteBuffer.allocateDirect(pointer * 4 * 2).order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            final float[] temp = new float[buffer.capacity()];
            buffer.get(temp);
            next.put(temp);
            buffer = next;

            System.gc();
        }
    }

    protected int getPointer() {
        return pointer;
    }

}
