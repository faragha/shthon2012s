package jp.preety.ispants.oekaki.render;

import jp.preety.ispants.oekaki.OekakiRender;
import jp.preety.ispants.oekaki.data.Data;

import com.eaglesakura.lib.android.game.graphics.ImageCorrector;
import com.eaglesakura.lib.android.game.graphics.gl11.SpriteManager;
import com.eaglesakura.lib.android.game.graphics.gl11.TextureImageBase;
import com.eaglesakura.lib.android.game.math.Vector2;

public class StampRender extends RenderShapeBase {

    public StampRender(OekakiRender render) {
        super(render);
    }

    public StampRender(OekakiRender render, Data _data) {
        super(render, _data);
    }

    @Override
    public void draw() {
        TextureImageBase pen = render.getDocument().getPenTexture(data.pen);
        SpriteManager spriteManager = render.getSpriteManager();

        spriteManager.begin();
        {
            Vector2 center = data.touchPoints.get(0);
            ImageCorrector baseImageCorrector = render.getDocument().getBaseImageCorrector();
            final float x = baseImageCorrector.uToImagePix(center.x);
            final float y = baseImageCorrector.vToImagePix(center.y);
            spriteManager.drawImage(pen, (int) x - pen.getWidth() / 2, (int) y - pen.getHeight() / 2);
        }
        spriteManager.end();
    }

}
