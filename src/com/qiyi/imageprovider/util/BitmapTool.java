package com.qiyi.imageprovider.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.SystemClock;

import com.qiyi.imageprovider.base.ImageRequest.ScaleType;
import com.qiyi.imageprovider.util.CornerSpecUtils.Corner;

public final class BitmapTool {
    private static final String TAG = "ImageProvider/BitmapTool";
    private static final Config DEFAULT_BITMAP_DECODE_CONFIG = Config.ARGB_8888;

    private static final HashMap<Corner, Integer> CORNER_ANGLE_MAP = new HashMap<Corner, Integer>();
    static {
        CORNER_ANGLE_MAP.put(Corner.TOP_LEFT, 180);
        CORNER_ANGLE_MAP.put(Corner.TOP_RIGHT, 270);
        CORNER_ANGLE_MAP.put(Corner.BOTTOM_LEFT, 90);
        CORNER_ANGLE_MAP.put(Corner.BOTTOM_RIGHT, 0);
    }
    
    private static Options createBasicOptions(Config config) {
        Options options = new Options();
        if (config != null) {
            options.inPreferredConfig = config;
            if (config != Config.ARGB_8888) {
                options.inDither = true;
            }
        }
        if (LogUtils.DEBUG) LogUtils.d(TAG, "createBasicOptions() return " + LogUtils.printBitmapOptions(options));
        return options;
    }
    
    /**
     * create bitmap from byte array
     * @param data bitmap data represented in byte array
     * @param config decode config
     * @return decoded bitmap
     */
    public static Bitmap createBitmap(byte[] data, Config config) {
        try {
            Options options = createBasicOptions(config);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            return bitmap;
        } catch (OutOfMemoryError ex) {
            LogUtils.e(TAG, "createBitmap: OOM", ex);
            return null;
        }
    }
    
    public static Bitmap createBitmap(byte[] data) {
        return createBitmap(data, DEFAULT_BITMAP_DECODE_CONFIG);
    }

    
    /**
     * Create bitmap with specified target w/h and {@link ScaleType}
     * @param data raw bitmap data
     * @param targetWidth desired width to reach
     * @param targetHeight desired height to reach
     * @param type desired scale type for scaling down (or not) the bitmap
     * @param config the bitmap config for decoding
     * @return scaled down (or not) bitmap
     */
    public static Bitmap createBitmap(byte[] data, int targetWidth, int targetHeight, ScaleType type, Config config) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, ">> createBitmap: target w/h=" + targetWidth + "/" + targetHeight + ", scale type=" + type + ", config=" + config);
        try {
            final Options dbo = new Options();
            dbo.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, dbo);

            final int nativeWidth = dbo.outWidth;
            final int nativeHeight = dbo.outHeight;
            if (LogUtils.DEBUG) LogUtils.d(TAG, "createBitmap: original w/h=" + nativeWidth + "/" + nativeHeight);
            float scaledWidth;
            float scaledHeight;
            final Options options = createBasicOptions(config);
            if (nativeWidth > targetWidth || nativeHeight > targetHeight) {
                float dx = ((float) nativeWidth) / ((float) targetWidth);
                float dy = ((float) nativeHeight) / ((float) targetHeight);
                float scale = (type == ScaleType.CENTER_INSIDE) ? Math.max(dx, dy) : Math.min(dx, dy);
                if (LogUtils.DEBUG) LogUtils.d(TAG, "createBitmap: scale=" + scale);
                scaledWidth = nativeWidth / scale;
                scaledHeight = nativeHeight / scale;
                // Create the bitmap from file.
                options.inSampleSize = (scale > 1.0f) ? (Math.round(scale)) : 1;
                if (LogUtils.DEBUG) LogUtils.d(TAG, "createBitmap: inSampleSize=" + options.inSampleSize);
            } else {
                scaledWidth = targetWidth;
                scaledHeight = targetHeight;
                options.inSampleSize = 1;
            }
            if (LogUtils.DEBUG) LogUtils.d(TAG, "createBitmap: scaled w/h=" + scaledWidth + "/" + scaledHeight);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (LogUtils.DEBUG) LogUtils.d(TAG, "<< createBitmap: result bitmap=" + LogUtils.printBitmap(bitmap));
            return bitmap;
        } catch (OutOfMemoryError ex) {
            LogUtils.e(TAG, "<< createBitmap: OOM", ex);
            return null;
        }
    }
    
    public static Bitmap createBitmap(byte[] data, int targetWidth, int targetHeight, ScaleType type) {
        return createBitmap(data, targetWidth, targetHeight, type, DEFAULT_BITMAP_DECODE_CONFIG);
    }

    /**
     * create bitmap with url
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public static Bitmap createBitmap(String url) {
        return createBitmap(url, DEFAULT_BITMAP_DECODE_CONFIG);
    }
    
    public static Bitmap createBitmap(String url, Config config) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, "createBitmap(url), config=" + config);
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            inputStream = new FileInputStream(url);
            bitmap = createBitmap(inputStream, config);
        } catch (Exception ex) {
            if (LogUtils.DEBUG) LogUtils.e(TAG, "createBitmap(url), exception happened:", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * create bitmap from inputStream
     * 
     * @param inputStream
     * @return
     * @throws
     */
    public static Bitmap createBitmap(InputStream inputStream) {
        return createBitmap(inputStream, DEFAULT_BITMAP_DECODE_CONFIG);
    }
    
    public static Bitmap createBitmap(InputStream inputStream, Config config) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, ">> createBitmap(" + inputStream.hashCode() + "): inputStream=" + inputStream + ", config=" + config);
        try {
            Options options = createBasicOptions(config);
            options.inPreferredConfig = config;
            if (LogUtils.DEBUG) LogUtils.d(TAG, "createBitmap(" + inputStream.hashCode() + "): options=" + options);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            if (LogUtils.DEBUG) LogUtils.d(TAG, "createBitmap(" + inputStream.hashCode() + "): decoded=" + LogUtils.printBitmap(bitmap));
            return bitmap;
        } catch (Exception ex) {
            if (LogUtils.DEBUG) LogUtils.e(TAG, "createBitmap(" + inputStream.hashCode() + "): exception happened", ex);
            return null;
        }
    }
    

    public static Bitmap toDefaultBitmap(byte[] data) {
        return createBitmap(data);
    }

    public static Bitmap toRoundForce(byte[] data, float radius) {
        Bitmap bitmap = createBitmap(data);
        if (radius <= 0) {
            return bitmap;
        }

        return toRoundBitmap(bitmap, radius);
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap, float radius) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, "toRoundBitmap(): radius=" + radius);
        Config origConfig = bitmap.getConfig();
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        bitmap = null;

        return convertConfigIfNeeded(output, origConfig);
    }

    public static Bitmap scaleBitmap(Bitmap original, int targetWidth, int targetHeight) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, ">> scaleBitmap: original=" + LogUtils.printBitmap(original)
                + ", target w/h=" + targetWidth + "/" + targetHeight);
        if (original == null) {
            return null;
        }
        int originalW = original.getWidth();
        int originalH = original.getHeight();
        if (originalW == targetWidth && originalH == targetHeight) {
            return original;
        }

        float destRatio = (float) targetWidth / targetHeight;
        float srcRatio = (float) originalW / originalH;
        int resultW = originalW;
        int resultH = originalH;
        Rect srcRect = null;
        if (destRatio >= srcRatio) {
            resultH = Math.round(resultW / destRatio);
            srcRect = new Rect(0, Math.round((float) (originalH - resultH) / 2), resultW, Math.round((float) originalH
                    / 2 + (float) resultH / 2));
        } else {
            resultW = Math.round(resultH * destRatio);
            srcRect = new Rect(Math.round((float) (originalW - resultW) / 2), 0, Math.round((float) originalW / 2
                    + (float) resultW / 2), resultH);
        }

        Bitmap newBmp = Bitmap.createBitmap(targetWidth, targetHeight, original.getConfig());
        Canvas c = new Canvas(newBmp);
        Paint p = new Paint(createPaintFlags(true, true, original.getConfig()));
        c.drawBitmap(original, srcRect, new Rect(0, 0, newBmp.getWidth(), newBmp.getHeight()), p);
        if (LogUtils.DEBUG) LogUtils.d(TAG, "<< scaleBitmap: returned bitmap=" + LogUtils.printBitmap(newBmp));
        return newBmp;
    }
    
    private static int createPaintFlags(boolean aa, boolean filter, Config config) {
        int paintFlags = 0;
        if (aa) {
            paintFlags |= Paint.ANTI_ALIAS_FLAG;
        }
        if (filter) {
            paintFlags |= Paint.FILTER_BITMAP_FLAG;
        }
        if (config != Config.ARGB_8888) {
            paintFlags |= Paint.DITHER_FLAG;
        }
        return paintFlags;
    }

    /****** per-corner processing begin @{ ******/
    public static Bitmap clipToRoundCorners(Bitmap bitmap, CornerSpecUtils.CornerSpec... specs) {
        if (bitmap == null) {
            return null;
        }
        if (specs == null || specs.length <= 0) {
            return bitmap;
        }
        if (LogUtils.DEBUG) LogUtils.d(TAG, ">> clipToRoundCorners(spec): specs=" + Arrays.toString(specs));

        // this output bitmap MUST have alpha channel to crop off pixels
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Rect fullRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF fullRectF = new RectF(fullRect);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        CornerSpecUtils.CornerSpec[] internalSpecs = processInputSpecs(specs);
        for (CornerSpecUtils.CornerSpec s : internalSpecs) {
            drawRoundCorneredMask(canvas, paint, getQuarterRect(fullRectF, s.getCorner()), s.getRadius(),
                    s.getCorner());
        }

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, fullRect, fullRectF, paint);

        long time = SystemClock.uptimeMillis();
        if (LogUtils.DEBUG) LogUtils.d(TAG, "<< clipToRoundCorners(spec): time consumed=" + (SystemClock.uptimeMillis() - time));
        // convert to original format if necessary
        return convertConfigIfNeeded(output, bitmap.getConfig());
    }
    
    private static Bitmap convertConfigIfNeeded(Bitmap bitmap, Config targetConfig) {
        if (bitmap.getConfig() == targetConfig) {
            return bitmap;
        }
        Bitmap ret = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), targetConfig);
        Canvas c = new Canvas(ret);
        c.drawBitmap(bitmap, 0, 0, new Paint(createPaintFlags(false, false, targetConfig)));
        return ret;
    }

    // convert input specs to internal-compatible specs (with all 4 corners and
    // no duplicate corners)
    private static CornerSpecUtils.CornerSpec[] processInputSpecs(CornerSpecUtils.CornerSpec... specs) {
        ArrayList<CornerSpecUtils.CornerSpec> specArray = new ArrayList<CornerSpecUtils.CornerSpec>();
        if (specs == null || specs.length <= 0) {
            return (CornerSpecUtils.CornerSpec[]) specArray.toArray();
        }

        // 1. remove duplicate corner
        HashMap<Corner, CornerSpecUtils.CornerSpec> cornerToSpecMap = new HashMap<Corner, CornerSpecUtils.CornerSpec>();
        for (CornerSpecUtils.CornerSpec s : specs) {
            if (s.getCorner() != null) {
                cornerToSpecMap.put(s.getCorner(), s);
            }
        }

        // 2. Add omitted corner
        for (Corner c : Corner.values()) {
            if (!cornerToSpecMap.containsKey(c)) {
                cornerToSpecMap.put(c, new CornerSpecUtils.CornerSpec(c, 0));
            }
        }
        return cornerToSpecMap.values().toArray(new CornerSpecUtils.CornerSpec[Corner.values().length]);
    }

    private static void drawRoundCorneredMask(Canvas canvas, Paint paint, RectF targetRectF, float radius,
            Corner corner) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, "drawRoundCorneredMask: target Rect w/h=" + targetRectF.width() + "/" + targetRectF.height()
                + ", radius=" + radius);
        if (corner == null) {
            canvas.drawRect(targetRectF, paint);
        } else {
            float targetLeft = targetRectF.left;
            float targetTop = targetRectF.top;
            float targetRight = targetRectF.right;
            float targetBottom = targetRectF.bottom;
            RectF oval = null;
            float side = radius * 2;
            switch (corner) {
            case TOP_LEFT:
                oval = new RectF(targetLeft, targetTop, targetLeft + side, targetTop + side);
                break;
            case TOP_RIGHT:
                oval = new RectF(targetRight - side, targetTop, targetRight, targetTop + side);
                break;
            case BOTTOM_LEFT:
                oval = new RectF(targetLeft, targetBottom - side, targetLeft + side, targetBottom);
                break;
            case BOTTOM_RIGHT:
                oval = new RectF(targetRight - side, targetBottom - side, targetRight, targetBottom);
                break;
            }
            canvas.drawArc(oval, CORNER_ANGLE_MAP.get(corner), 90, true, paint);
            fillExclusiveRect(canvas, paint, targetRectF, getQuarterRect(oval, corner));
        }
    }

    private static void fillExclusiveRect(Canvas canvas, Paint paint, RectF entire, RectF excluded) {
        int sc = canvas.saveLayer(entire, paint,
                Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG
                | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        Xfermode oldMode = paint.getXfermode();
        paint.setXfermode(null);
        canvas.drawRect(excluded, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.XOR));
        canvas.drawRect(entire, paint);
        paint.setXfermode(oldMode);
        canvas.restoreToCount(sc);
    }

    private static RectF getQuarterRect(RectF fullRectF, Corner corner) {
        if (corner == null) {
            return fullRectF;
        }

        float width = fullRectF.right - fullRectF.left;
        float height = fullRectF.bottom - fullRectF.top;
        RectF ret = null;
        switch (corner) {
        case TOP_LEFT:
            ret = new RectF(fullRectF.left, fullRectF.top, fullRectF.left + width / 2, fullRectF.top + height / 2);
            break;
        case TOP_RIGHT:
            ret = new RectF(fullRectF.left + width / 2, fullRectF.top, fullRectF.right, fullRectF.top + height / 2);
            break;
        case BOTTOM_LEFT:
            ret = new RectF(fullRectF.left, fullRectF.top + height / 2, fullRectF.left + width / 2, fullRectF.bottom);
            break;
        case BOTTOM_RIGHT:
            ret = new RectF(fullRectF.left + width / 2, fullRectF.top + height / 2, fullRectF.right, fullRectF.bottom);
            break;
        }
        Rect r = new Rect();
        ret.roundOut(r);
        return new RectF(r);
    }
    /****** per-corner processing end @} ******/
    
    // rotation; only supports multiple of 90 degrees, positive and negative 
    public static Bitmap rotateBitmap(Bitmap original, int degree) {
        if (degree == 0) {
            return original;
        }
        
        if (degree % 90 != 0) {
            return original;
        }
        
        int width = original.getWidth();
        int height = original.getHeight();
        if (LogUtils.DEBUG) LogUtils.d(TAG, "rotateBitmap: original size=" + width + "/" + height);
        
        Matrix m = new Matrix();
        m.postRotate(degree);
        Bitmap ret = Bitmap.createBitmap(original, 0, 0, width, height, m, true);
        if (LogUtils.DEBUG) LogUtils.d(TAG, "rotateBitmap: rotated size=" + ret.getWidth() + "/" + ret.getHeight());
        return ret;
    }

}
