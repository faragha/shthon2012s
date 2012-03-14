
package me.suta.android.sutame.image;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.media.ExifInterface;

/**
 * ExifInterfaceをAndroid 1.6(API Level 4)以下で使用できるようにするユーティリティークラス。
 */
public class ExifInterfaceUtil {
    /**
     * 画像のExif情報から回転角度を取得する
     * 
     * <pre>
     * // Original code for Android 2.1(API Level 5 Later)
     * int rotation = 0;
     * try {
     *     ExifInterface exif = new ExifInterface(fileName);
     *     int orientation = exif.getAttributeInt(
     *               ExifInterface.TAG_ORIENTATION, 1);
     *     switch (orientation) {
     *         case 6:
     *             rotation = 90;
     *             break;
     *     }
     * } catch (IOException e) {
     *     e.printStackTrace();
     * }
     * </pre>
     * 
     * @param fileName 画像のファイル名（フルパス）
     * @return 回転角度（0、90、180、270）※単位は度
     */
    public static int getRotation(String fileName) {
        int rotation = 0;
        try {
            Class<?> clazz = ExifInterface.class;
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Object instance = constructor.newInstance(fileName);
            Method method = clazz.getMethod("getAttributeInt", String.class,
                    int.class);
            if (method != null) {
                rotation = (Integer) method.invoke(instance, "Orientation");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return rotation;
    }

}
