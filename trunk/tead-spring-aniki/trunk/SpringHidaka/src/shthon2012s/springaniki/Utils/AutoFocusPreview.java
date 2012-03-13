
package shthon2012s.springaniki.Utils;

import shthon2012s.springaniki.ActCamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

public class AutoFocusPreview extends CameraPreview {

    ActCamera AFS;

    public AutoFocusPreview(Context context, ActCamera _AFS) {
        super(context);
        AFS = _AFS;
    }

    public void autoFocus() {
        camera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, final Camera camera) {
                ShutterCallback shutter = new ShutterCallback() {
                    @Override
                    public void onShutter() {
                        DBG.LogOut(3, "TEST", "onShutter");
                    }
                };
                PictureCallback raw = new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        DBG.LogOut(3, "TEST", "onPictureTaken: raw: data=" + data);
                    }
                };
                PictureCallback jpeg = new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        DBG.LogOut(3, "TEST", "onPictureTaken: jpeg: data=" + data);
                        Bitmap bmp = data2bmp(data);

                        AFS.GotBitmap(bmp);

                    }
                };
                camera.takePicture(shutter, raw, jpeg);
            }
        });
    }

    void cancelAutoFocus() {
        camera.cancelAutoFocus();
    }

    private static Bitmap data2bmp(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
