package jp.preety.ispants.oekaki.gesture;

import jp.preety.ispants.oekaki.OekakiRender;
import jp.preety.ispants.oekaki.data.Pen;
import jp.preety.ispants.oekaki.data.Pen.Type;
import jp.preety.ispants.oekaki.render.RenderShapeBase;
import jp.preety.ispants.oekaki.render.TegakiLineRender;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.eaglesakura.lib.android.game.graphics.ImageCorrector;
import com.eaglesakura.lib.android.game.util.LogUtil;

/**
 * 
 * @author TAKESHI YAMASHITA
 *
 */
public class GestureController implements android.view.GestureDetector.OnGestureListener {

    /**
     * ディテクタ
     */
    GestureDetector detector = new GestureDetector(this);

    /**
     * 描画用のシェイプ
     */
    RenderShapeBase currentShape = null;

    OekakiRender render = null;

    public GestureController(OekakiRender render) {
        this.render = render;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public void onTouchBegin(MotionEvent e) {
        currentShape = TegakiLineRender.createInstance(render);
    }

    public void onTouchEnd(MotionEvent e) {
        render.getDocument().addShape(currentShape);
        //        render.getDocument().getServer().add(Data.toSendingData(currentShape.getData()));
        currentShape = null;
    }

    public boolean isTegaki() {
        return getPen().type == Type.Tegaki;
    }

    public boolean isStamp() {
        return getPen().type == Type.Stamp;
    }

    Pen getPen() {
        return currentShape.getData().pen;
    }

    /**
     * タッチイベントを受け取る
     * @param event
     */
    public void onTouch(MotionEvent event) {
        detector.onTouchEvent(event);

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_OUTSIDE) {
            onTouchEnd(event);
            render.rendering();
        } else if (action == MotionEvent.ACTION_DOWN) {
            onTouchBegin(event);
        } else if (action == MotionEvent.ACTION_MOVE) {

            // 手書きだったらライン書く
            if (isTegaki()) {
                ImageCorrector baseImageCorrector = render.getDocument().getBaseImageCorrector();
                final float u = baseImageCorrector.pixToImageU(event.getX());
                final float v = baseImageCorrector.pixToImageV(event.getY());

                LogUtil.log("u :: " + u);
                LogUtil.log("v :: " + v);

                currentShape.put(u, v);
                render.rendering();
            }
        }
    }

    /**
     * ジェスチャに関わる範囲を描画する。
     */
    public void draw() {
        if (currentShape != null) {
            currentShape.draw();
        }
    }
}
