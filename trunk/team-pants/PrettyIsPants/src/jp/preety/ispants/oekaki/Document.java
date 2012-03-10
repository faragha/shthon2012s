package jp.preety.ispants.oekaki;

import java.io.IOException;

import jp.preety.ispants.oekaki.data.Pen;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;

import com.eaglesakura.lib.android.game.display.VirtualDisplay;
import com.eaglesakura.lib.android.game.graphics.BigImage;
import com.eaglesakura.lib.android.game.graphics.ImageCorrector;
import com.eaglesakura.lib.android.game.graphics.gl11.BitmapTextureImage;
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

    public Document(OekakiRender render) {
        this.render = render;
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
}
