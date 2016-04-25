package com.qiyi.imageprovider.util.exif;

import java.io.IOException;

import com.qiyi.imageprovider.util.LogUtils;


public class ExifTool {
    private static final String TAG = "ImageProvider/ExifTool";
    
    public static ExifInterface getExif(byte[] jpegData) {
        ExifInterface exif = new ExifInterface();
        try {
            exif.readExif(jpegData);
        } catch (IOException e) {
            if (LogUtils.DEBUG) LogUtils.w(TAG, "Failed to read EXIF data", e);
        }
        return exif;
    }
    // Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
    public static int getOrientation(ExifInterface exif) {
        Integer val = exif.getTagIntValue(ExifInterface.TAG_ORIENTATION);
        if (val == null) {
            return 0;
        } else {
            return ExifInterface.getRotationForOrientationValue(val.shortValue());
        }
    }
    public static int getOrientation(byte[] jpegData) {
        if (jpegData == null) return 0;
        int orientation = 0;
        try {
            ExifInterface exif = getExif(jpegData);
            orientation = getOrientation(exif);
        } catch (Exception e) {
            if (LogUtils.DEBUG) LogUtils.d(TAG, "getOrientation: exception happened", e);
        }
        return orientation;
    }
}
