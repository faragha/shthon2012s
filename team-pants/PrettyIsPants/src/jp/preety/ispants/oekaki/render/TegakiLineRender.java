package jp.preety.ispants.oekaki.render;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import jp.preety.ispants.oekaki.OekakiRender;
import jp.preety.ispants.oekaki.data.Data;

import com.eaglesakura.lib.android.game.graphics.ImageCorrector;

public class TegakiLineRender extends RenderShapeBase {

    /**
     * データを新規作成する。
     * @param render
     */
    protected TegakiLineRender(OekakiRender render) {
        super(render);
    }

    /**
     * データを復元する。
     * @param render
     * @param data
     */
    protected TegakiLineRender(OekakiRender render, Data _data) {
        super(render, _data);
    }

    @Override
    public void draw() {
        final int num = getPointNum();
        if (num < 2) {
            return;
        }
        final GL11 gl = render.getGLManager().getGL();
        gl.glLoadIdentity();
        {
            ImageCorrector baseImageCorrector = render.getDocument().getBaseImageCorrector();
            float areaHeight = baseImageCorrector.getRenderAreaHeight();
            float imageHeight = baseImageCorrector.getImageAreaBottom() - baseImageCorrector.getImageAreaTop();

            float areaWidth = baseImageCorrector.getRenderAreaWidth();
            float imageWidth = baseImageCorrector.getImageAreaRight() - baseImageCorrector.getImageAreaLeft();

            gl.glScalef(imageWidth / areaWidth, imageHeight / areaHeight, 1);
        }
        gl.glColor4f(data.pen.color.r, data.pen.color.g, data.pen.color.b, data.pen.color.a);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, getBuffer());
        gl.glLineWidth(data.pen.width);
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, num);
    }

}
