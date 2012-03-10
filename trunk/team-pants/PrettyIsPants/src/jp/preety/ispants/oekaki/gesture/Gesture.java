package jp.preety.ispants.oekaki.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 
 * @author TAKESHI YAMASHITA
 *
 */
public class Gesture implements android.view.GestureDetector.OnGestureListener {
    GestureDetector detector = new GestureDetector(this);

    public Gesture() {
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

    public void onTouchEnd(MotionEvent e) {

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
        }
    }
}
