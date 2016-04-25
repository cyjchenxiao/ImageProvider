package com.qiyi.imageprovider.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class LogUtils {
    public static boolean DEBUG = DebugOptions.isOpenDebugLog();
    private LogUtils() {}
    public static int d(String tag, String msg) {
        return Log.d(tag, msg);
    }
    
    public static int d(String tag, String msg, Throwable tr) {
        return Log.d(tag, msg, tr);
    }
    
    public static int w(String tag, String msg) {
        return Log.w(tag, msg);
    }
    
    public static int w(String tag, String msg, Throwable tr) {
        return Log.w(tag, msg, tr);
    }
    
    public static int e(String tag, String msg) {
        return Log.e(tag, msg);
    }
    
    public static int e(String tag, String msg, Throwable tr) {
        return Log.e(tag, msg, tr);
    }
    
    public static int i(String tag, String msg) {
        return Log.i(tag, msg);
    }
    
    public static int i(String tag, String msg, Throwable tr) {
        return Log.i(tag, msg, tr);
    }
    
    public static String printBitmap(Bitmap bitmap) {
        StringBuilder builder = new StringBuilder();
        if (bitmap == null) {
            builder.append("{NULL}");
        } else {
            builder.append("{Bitmap@").append(bitmap.hashCode())
                .append("[").append(bitmap.getWidth()).append("x").append(bitmap.getHeight()).append("], config=" + bitmap.getConfig() + "}");
        }
        return builder.toString();
    }
    
    public static String printBitmapOptions(BitmapFactory.Options opt) {
        if (opt == null) {
            return "{NULL}";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Options@").append(opt.hashCode()).append("[");
        builder.append("inDither=").append(opt.inDither);
        builder.append(", inJustDecodeBounds=").append(opt.inJustDecodeBounds);
        builder.append(", inSampleSize=").append(opt.inSampleSize);
        builder.append(", inPreferredConfig=").append(opt.inPreferredConfig);
        builder.append("]");
        
        return builder.toString();
    }
}
