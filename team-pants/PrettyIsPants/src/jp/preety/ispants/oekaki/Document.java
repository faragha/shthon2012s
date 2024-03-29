package jp.preety.ispants.oekaki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.preety.ispants.R;
import jp.preety.ispants.oekaki.data.Data;
import jp.preety.ispants.oekaki.data.DataServer;
import jp.preety.ispants.oekaki.data.DataServer.OnDataUpdateListener;
import jp.preety.ispants.oekaki.data.Pen;
import jp.preety.ispants.oekaki.data.Pen.Type;
import jp.preety.ispants.oekaki.render.RenderShapeBase;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;

import com.eaglesakura.lib.android.game.display.VirtualDisplay;
import com.eaglesakura.lib.android.game.graphics.BigImage;
import com.eaglesakura.lib.android.game.graphics.ImageCorrector;
import com.eaglesakura.lib.android.game.graphics.gl11.BitmapTextureImage;
import com.eaglesakura.lib.android.game.graphics.gl11.OpenGLManager;
import com.eaglesakura.lib.android.game.graphics.gl11.SpriteManager;
import com.eaglesakura.lib.android.game.graphics.gl11.TextureImageBase;
import com.eaglesakura.lib.android.game.math.Vector2;

/**
 * 
 * @author TAKESHI YAMASHITA
 *
 */
public class Document implements OnDataUpdateListener {
    Pen pen;
    OekakiRender render;
    TextureImageBase baseImage = null;
    ImageCorrector baseImageCorrector = new ImageCorrector();
    DataServer server = null;
    /**
     * スタンプ用のテクスチャ
     */
    Map<Object, TextureImageBase> stampTextures = new HashMap<Object, TextureImageBase>();

    /**
     * 美白値
     * 0〜100で指定する。
     */
    float bihakuLevel = 0;

    /**
     * 追加済みの落書きシェイプデータ
     */
    List<RenderShapeBase> shapeDatas = new ArrayList<RenderShapeBase>();

    public Document(OekakiRender render) {
        this.render = render;
        server = new DataServer(render);
        server.addListener(this);

        //! 適当なペンを作成する
        {
            Pen pen = new Pen();
            pen.setTegakiData(5, 255, 255, 255);
            setPen(pen);
        }
    }

    public void setBihakuLevel(float bihakuLevel) {
        this.bihakuLevel = bihakuLevel;
    }

    public float getBihakuLevel() {
        return bihakuLevel;
    }

    /**
     * テクスチャの読み込みを行う。
     * @param resources
     * @param drawableId
     * @param glManager
     * @return
     */
    public static TextureImageBase loadImage(Resources resources, int drawableId, OpenGLManager glManager) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawableId);
        if (bitmap == null) {
            return null;
        }
        return new BitmapTextureImage(bitmap, glManager);
    }

    /**
     * 描画用の画像を取得する。
     * @param context
     * @param uri
     */
    public void loadBaseImage(Context context, Uri uri, VirtualDisplay virtualDisplay) throws IOException {
        Bitmap bitmap = BigImage.loadBitmap(context.getContentResolver(), uri, 1024 * 1024, new Vector2());
        baseImage = new BitmapTextureImage(bitmap, render.getGLManager());
        baseImageCorrector.setRenderArea(virtualDisplay);
        baseImageCorrector.setImageAspect(bitmap.getWidth(), bitmap.getHeight());
        bitmap.recycle();
    }

    void addStamp(Context context, int drawableId) {
        if (stampTextures.get(drawableId) != null) {
            return;
        }
        TextureImageBase image = loadImage(context.getResources(), drawableId, render.getGLManager());
        stampTextures.put(drawableId, image);
    }

    public void loadPenTextuers(Context context) {
        {
            addStamp(context, R.drawable.ic_launcher);
        }
    }

    /**
     * ペンに適したテクスチャを読み出す。
     * @param pen
     * @return
     */
    public TextureImageBase getPenTexture(Pen pen) {
        TextureImageBase result = null;
        if (pen.type == Type.Stamp) {
            int id = pen.getStampResId(render.getActivity());
            addStamp(render.getActivity(), id);
            result = stampTextures.get(id);

            if (result == null) {
                result = stampTextures.get(R.drawable.ic_launcher);
            }
            return result;
        }

        return null;
    }

    /**
     * 基本画像の描画を行う。
     */
    public void drawBaseImage(SpriteManager spriteManager) {
        Rect area = baseImageCorrector.getImageArea(new Rect());
        spriteManager.drawImage(baseImage, 0, 0, baseImage.getWidth(), baseImage.getHeight(), area.left, area.top,
                area.width(), area.height(), 0, 0xffffffff);
    }

    /**
     * シェイプの描画を行う。
     * @param shape
     */
    public void drawServerShapes(SpriteManager spriteManager) {
        for (RenderShapeBase shape : shapeDatas) {
            shape.draw();
        }
    }

    public void drawYohaku(SpriteManager spriteManager) {
        spriteManager.begin();
        {
            Rect area = baseImageCorrector.getImageArea(new Rect());

            if (baseImageCorrector.isXLongImage()) {
                spriteManager.fillRect(0, 0, area.right, area.top, 0x000000ff);
                spriteManager.fillRect(area.left, area.bottom, (int) baseImageCorrector.getRenderAreaRight(),
                        (int) baseImageCorrector.getRenderAreaBottom(), 0x000000ff);
            } else if (baseImageCorrector.isYLongImage()) {
                spriteManager.fillRect(0, 0, area.left, area.bottom, 0x000000ff);
                spriteManager.fillRect(area.right, area.top, (int) baseImageCorrector.getRenderAreaRight(),
                        (int) baseImageCorrector.getRenderAreaBottom(), 0x000000ff);
            }
        }
        spriteManager.end();
    }

    /**
     * 描画用のペンを取得する。
     * @return
     */
    public Pen getPen() {
        return pen;
    }

    /**
     * 描画用のペンを設定する。
     * 設定後、このPenはRead-Onlyにしておくこと。
     * @param pen
     */
    public void setPen(Pen pen) {
        if (pen.type == Type.Bihaku) {
            final Data data = new Data();
            data.pen = pen;

            render.getRenderHandler().post(new Runnable() {
                @Override
                public void run() {
                    getServer().add(data);
                    setBihakuLevel(data.pen.bihakuLevel);
                }
            });
            return;
        }

        this.pen = pen;
    }

    public TextureImageBase getBaseImage() {
        return baseImage;
    }

    /**
     * サーバーを取得する。
     * @return
     */
    public DataServer getServer() {
        return server;
    }

    /**
     * シェイプをスタックに追加する。
     * @param shape
     */
    public void addShape(final RenderShapeBase shape) {
        if (!render.isRenderThread()) {
            render.getRenderHandler().post(new Runnable() {
                @Override
                public void run() {
                    addShape(shape);
                }
            });
            return;
        }
        shapeDatas.add(shape);
        server.add(shape.getData());
    }

    /**
     * イメージ位置管理
     * @return
     */
    public ImageCorrector getBaseImageCorrector() {
        return baseImageCorrector;
    }

    @Override
    public void onDataAdded(DataServer server, Data data, String json) {
        //! データを他のデバイスに転送する
        render.getActivity().sendToOtherDevice(json);
        render.rendering();
    }

    @Override
    public void onDataReceve(final DataServer server, final Data data) {
        if (!render.isRenderThread()) {
            render.getRenderHandler().post(new Runnable() {
                @Override
                public void run() {
                    onDataReceve(server, data);
                }
            });
            return;
        }

        if (data.pen.type == Type.Bihaku) {
            setBihakuLevel(data.pen.bihakuLevel);
            render.rendering();
            return;
        }

        //  
        {
            RenderShapeBase shape = RenderShapeBase.createInstance(render, data);
            shapeDatas.add(shape);
        }

        render.rendering();
    }
}
