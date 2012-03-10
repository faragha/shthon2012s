package jp.preety.ispants.oekaki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.preety.ispants.R;
import jp.preety.ispants.oekaki.data.DataServer;
import jp.preety.ispants.oekaki.data.Pen;
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
public class Document {
    Pen pen;
    OekakiRender render;
    TextureImageBase baseImage = null;
    ImageCorrector baseImageCorrector = new ImageCorrector();
    DataServer server = null;

    List<TextureImageBase> penTextures = new ArrayList<TextureImageBase>();

    public Document(OekakiRender render) {
        this.render = render;
        server = new DataServer(render);

        //! 適当なペンを作成する
        {
            Pen pen = new Pen();
            pen.setTegakiData(5, 0, 0, 255);
            setPen(pen);
        }
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
        return new BitmapTextureImage(bitmap, glManager);
    }

    /**
     * 描画用の画像を取得する。
     * @param context
     * @param uri
     */
    public void loadBaseImage(Context context, Uri uri, VirtualDisplay virtualDisplay) throws IOException {
        Bitmap bitmap = BigImage.loadBitmap(context.getContentResolver(), uri, BigImage.FULL_HD, new Vector2());
        baseImage = new BitmapTextureImage(bitmap, render.getGLManager());
        baseImageCorrector.setRenderArea(virtualDisplay);
        baseImageCorrector.setImageAspect(bitmap.getWidth(), bitmap.getHeight());
    }

    public void loadPenTextuers(Context context) {
        penTextures.add(loadImage(context.getResources(), R.drawable.pen, render.getGLManager()));
    }

    /**
     * ペンに適したテクスチャを読み出す。
     * @param pen
     * @return
     */
    public TextureImageBase getPenTexture(Pen pen) {
        return penTextures.get(0);
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
        this.pen = pen;
    }

    public TextureImageBase getBaseImage() {
        return baseImage;
    }

    /**
     * イメージ位置管理
     * @return
     */
    public ImageCorrector getBaseImageCorrector() {
        return baseImageCorrector;
    }
}
