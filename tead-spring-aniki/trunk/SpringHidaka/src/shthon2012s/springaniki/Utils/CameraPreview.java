
package shthon2012s.springaniki.Utils;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    protected Context context;

    private SurfaceHolder holder;

    protected Camera camera;

    public int previewW = 0;

    public int previewH = 0;

    public int pictureW = Params.pictureWH;

    public int pictureH = Params.pictureWH;

    public int minPictureWH = Params.minPictureWH;

    CameraPreview(Context context) {
        super(context);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DBG.LogOut(3, "TEST", "surfaceCreated");
        if (camera == null) {
            try {
                camera = Camera.open();
            } catch (RuntimeException e) {
                ((Activity) context).finish();
            }
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            camera.release();
            camera = null;
            ((Activity) context).finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        DBG.LogOut(3, "TEST", "surfaceChanged+:" + width + ":" + height);
        if (camera == null) {
            ((Activity) context).finish();
        } else {
            camera.stopPreview();
            setPictureFormat(format);
            setPreviewSize(width, height);
            setPictureSize();
            camera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        DBG.LogOut(3, "TEST", "surfaceDestroyed");
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    protected void setPictureFormat(int format) {
        try {
            Camera.Parameters params = camera.getParameters();
            List<Integer> supported = params.getSupportedPictureFormats();
            if (supported != null) {
                for (int f : supported) {
                    if (f == format) {
                        params.setPreviewFormat(format);
                        camera.setParameters(params);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setPreviewSize(int width, int height) {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> supported = params.getSupportedPreviewSizes();
        if (supported != null) {
            DBG.LogOut(3, "setPreviewSize:base", width + ":" + height);
            for (Camera.Size size : supported) {
                // 3:4のやつだけ
                if (size.width * 3 == size.height * 4) {
                    DBG.LogOut(3, "setPreviewSizeWH", size.width + ":" + size.height);
                    // プレビューより小さくてできるだけ大きいの使う
                    if (size.width <= width && size.height <= height) {
                        params.setPreviewSize(size.width, size.height);
                        camera.setParameters(params);
                        previewW = size.width;
                        previewH = size.height;
                        DBG.LogOut(3, "setPictureSizeSET", "H:" + size.width + ":W:" + size.height);
                        break;
                    }
                }
            }
        }
    }

    private void setPictureSize() {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> supported = params.getSupportedPictureSizes();
        if (supported != null) {
            int tmpW = 0;
            int tmpH = 0;
            for (Camera.Size size : supported) {
                // 3:4のやつだけ
                if (size.width * 3 == size.height * 4) {
                    DBG.LogOut(3, "setPictureSize", size.width + ":" + size.height);
                    // 最小より大きくて、できるだけ小さいの使う
                    if (size.width > minPictureWH && size.height > minPictureWH) {
                        tmpW = size.width;
                        tmpH = size.height;
                        if (pictureW > tmpW && pictureH > tmpH) {
                            pictureW = tmpW;
                            pictureH = tmpH;
                        }

                    }
                }
            }
            // もし3:4がひとつもなかったら2:3をリサイズとか
            params.setPictureSize(pictureW, pictureH);
            camera.setParameters(params);
        }
    }

}
