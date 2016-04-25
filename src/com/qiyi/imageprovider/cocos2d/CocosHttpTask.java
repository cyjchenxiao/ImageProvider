package com.qiyi.imageprovider.cocos2d;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.base.ImageRequest.ImageType;
import com.qiyi.imageprovider.base.ImageRequest.ScaleType;
import com.qiyi.imageprovider.logic.ImageProvider;
import com.qiyi.imageprovider.logic.cache.Cache;
import com.qiyi.imageprovider.logic.task.ITaskStatusListener;
import com.qiyi.imageprovider.model.USAException;
import com.qiyi.imageprovider.util.BitmapTool;
import com.qiyi.imageprovider.util.DebugOptions;
import com.qiyi.imageprovider.util.LogUtils;
import com.qiyi.imageprovider.util.StringUtils;
import com.qiyi.imageprovider.util.UrlTool;
import com.qiyi.imageprovider.util.exif.ExifTool;

public class CocosHttpTask implements Runnable, Serializable {
    private static final String TAG_S = "ImageProvider/CocosHttpTask";
    protected static final int CONN_TIMEOUT = 4000;
    protected static final int READ_TIMEOUT = 8000;
    protected static final int RETRY_CONN_TIMEOUT = 6000;
    protected static final int RETRY_READ_TIMEOUT = 15000;
    protected static final int TOTAL_RETRY_COUNT = 2;
    
    private final String TAG;

    private static final long serialVersionUID = 1L;

    private int mRetryCounter = 0;
    private int mReadTimeout = 0;
    private int mConnectTimeout = 0;
    private boolean mIsDeprecated = false;

    public ImageRequest mImageRequest;
    private Cache mCache = Cache.get();
    private String mHttpCode;

    private ITaskStatusListener mTaskStatusListener;
    private ICocosCallback mCocosCallback;

    public CocosHttpTask(final ImageRequest imageRequest, ITaskStatusListener listener, ICocosCallback cocosCallback) {
        TAG = "ImageProvider/HttpTask@" + Integer.toHexString(hashCode());
        LogUtils.d(TAG, "<init>, request=" + imageRequest);
        mImageRequest = imageRequest;
        mCocosCallback = cocosCallback;
        mReadTimeout = getReadTimeout();
        mConnectTimeout = getConnTimeout();
        mTaskStatusListener = listener;
    }

    protected int getTotalRetryCount() {
        return TOTAL_RETRY_COUNT;
    }

    protected int getConnTimeout() {
        return CONN_TIMEOUT;
    }

    protected int getReadTimeout() {
        return READ_TIMEOUT;
    }

    protected int getRetryConnTimeout() {
        return RETRY_CONN_TIMEOUT;
    }

    protected int getRetryReadTimeout() {
        return RETRY_READ_TIMEOUT;
    }

    protected ImageRequest getImageRequest() {
        return mImageRequest;
    }

    public void deprecate() {
        if (LogUtils.DEBUG) LogUtils.d(TAG, "deprecate");
        mIsDeprecated = true;
    }

    public boolean isEquals(String url) {
        return mImageRequest.getUrl().equals(url);
    }
    
    public boolean isEquals(ImageRequest request) {
        return mImageRequest.equals(request);
    }

    @Override
    public void run() {
        long start = SystemClock.uptimeMillis();
        LogUtils.d(TAG, ">> run(), retry #" + mRetryCounter);
        if (mIsDeprecated) {
            LogUtils.w(TAG, "<< run(): deprecated");
            return;
        }
        
        if (!ImageRequest.checkRequestValid(mImageRequest)) {
            LogUtils.e(TAG, "<< run(): invalid request: " + mImageRequest);
            if (!mIsDeprecated) {
                failure(new USAException(USAException.PARAMS_ERROR));
            }
            return;
        }

        ImageProvider.get().addRunningTask(this);

        boolean isSuccess = false;
        mHttpCode = "";
        Throwable exception = null;
        try {
            if (DebugOptions.isEnableFileCache()) {
                isSuccess = checkFile(mImageRequest);
                LogUtils.d(TAG, "run: checkFile returns " + isSuccess);
            }
            if (!isSuccess) {
                isSuccess = httpRequest(mImageRequest.getUrl());
                LogUtils.d(TAG, "run: httpRequest returns " + isSuccess);
            }
        } catch (Exception ex) {
        	LogUtils.d(TAG, "run: exception happend:", ex);
            exception = ex;
        } catch (AssertionError error) {
        	LogUtils.d(TAG, "run: exception happend:", error);
            exception = error;
        }

        ImageProvider.get().removeRunningTask(this);

        // try again
        if (!isSuccess) {
            // retry
            boolean willRetry = callRetry();

            // send fail pingback
            if (!willRetry && mTaskStatusListener != null) {
                mTaskStatusListener.onHttpRequestFailed(mImageRequest, exception, mHttpCode);
            }
        }
        LogUtils.d(TAG, "<< run(), time consumed=" + (SystemClock.uptimeMillis() - start));
    }

    /**
     * check file whether exist image
     * 
     * @param url
     * @return
     */
    protected boolean checkFile(ImageRequest request) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, ">> checkFile()");
        String savePath = mCache.getFileBitmapPath(request);
        if (!StringUtils.isEmpty(savePath)) {
            notifyUISuccess(savePath);
            return true;
        }
        LogUtils.e(TAG, ">>【checkFile false】：url:" + request.getUrl());
        return false;
    }

    /**
     * http request
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws AssertionError
     */
    private boolean httpRequest(String url) throws ClientProtocolException, IOException, AssertionError {
        long start = SystemClock.uptimeMillis();
        LogUtils.d(TAG, ">> httpRequest(): conn TO=" + mConnectTimeout + ", read TO="
                + mReadTimeout);
        boolean isSuccess = false;
        String suffixedUrl = appendSuffix(url);
        if (LogUtils.DEBUG) LogUtils.d(TAG, "httpRequest: suffixed url=" + suffixedUrl);
        HttpGet httpget = new HttpGet(suffixedUrl);
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, mConnectTimeout);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, mReadTimeout);
        HttpResponse response = null;
        response = httpClient.execute(httpget);
        LogUtils.d(TAG, "httpRequest: response code=" + response.getStatusLine().getStatusCode()
                + ", request time consumed=" + (SystemClock.uptimeMillis() - start));
        int httpCode = response.getStatusLine().getStatusCode();
        mHttpCode = String.valueOf(httpCode);
        byte[] bytes = null;
        if (httpCode == HttpStatus.SC_OK) {
            bytes = readStream(response);
            if (bytes != null && bytes.length != 0) {
                isSuccess = save(url, bytes);
            }
        }
        httpClient.getConnectionManager().shutdown();
        LogUtils.d(TAG, "<< httpRequest(), total time consumed=" + (SystemClock.uptimeMillis() - start)
                + ", download bytes=" + (bytes == null ? -1 : bytes.length));
        return isSuccess;
    }

    private static String appendSuffix(String url) {
        StringBuilder ret = new StringBuilder(url);
        Uri uri = Uri.parse(url);
        boolean hasQueryString = uri != null && !StringUtils.isEmpty(uri.getQuery());
        String queryString = null;
        if (DebugOptions.isAddSuffixForUrl()) {
            String requestSuffix = UrlTool.getRequestSuffix();
            if (LogUtils.DEBUG) LogUtils.d(TAG_S, "appendSuffix: request suffix={" + requestSuffix + "}");
            queryString = (hasQueryString ? "&" : "?") + UrlTool.getRequestSuffix();
            if (LogUtils.DEBUG) LogUtils.d(TAG_S, "appendSuffix: queryString=" + queryString);
            ret.append(queryString);
        }
        return ret.toString();
    }

    private byte[] readStream(HttpResponse response) throws IOException {
        LogUtils.d(TAG, ">> readStream()");
        long readStart = SystemClock.uptimeMillis();
        HttpEntity entity = response.getEntity();
        BufferedInputStream fileInStream = new BufferedInputStream(entity.getContent());
        long contentLength = entity.getContentLength();
        if (contentLength < 0) {
        	LogUtils.e(TAG, ">> Error: entity.getContentLength() < 0 !!!");
			return null;
		}
        byte[] bytesBuffer = new byte[(int) contentLength];
        int bytesTotalSize = 0;
        int readBytesSize = 0;
        int readFinishByteSize = 0;
        bytesTotalSize = bytesBuffer.length;
        while ((readBytesSize = fileInStream.read(bytesBuffer, readFinishByteSize, bytesTotalSize - readFinishByteSize)) > 0) {
            readFinishByteSize += readBytesSize;
        }
        fileInStream.close();
        if (readFinishByteSize != contentLength) {
            throw new IOException();
        }
        LogUtils.d(TAG, "<< readStream(), total time consumed=" + (SystemClock.uptimeMillis() - readStart));
        return bytesBuffer;
    }

    /**
     * save image
     * 
     * @param url
     * @param bytesBuffer
     * @return
     */
    protected boolean save(final String url, final byte[] bytesBuffer) {
        long saveStart = SystemClock.uptimeMillis();
        LogUtils.d(TAG, ">> save()");
        Bitmap bitmap = null;

        // bitmap creation
        if (mImageRequest.getScaleType() == ScaleType.CENTER_INSIDE) {
            bitmap = BitmapTool.createBitmap(bytesBuffer, mImageRequest.getTargetWidth(),
                    mImageRequest.getTargetHeight(), ScaleType.CENTER_INSIDE, mImageRequest.getDecodeConfig());
        } else if (mImageRequest.getScaleType() == ScaleType.CENTER_CROP) {
            long start = SystemClock.uptimeMillis();
            if (LogUtils.DEBUG) LogUtils.d(TAG, "save => scale() ");
            bitmap = BitmapTool.createBitmap(bytesBuffer, mImageRequest.getTargetWidth(),
                    mImageRequest.getTargetHeight(), ScaleType.CENTER_CROP, mImageRequest.getDecodeConfig());
            // scale first to maintain the original size of the rounded corner
            // TODO move force scale to round part
            bitmap = BitmapTool.scaleBitmap(bitmap, mImageRequest.getTargetWidth(), mImageRequest.getTargetHeight());
            if (LogUtils.DEBUG) LogUtils.d(TAG, "save <= scale(), time consumed=" + (SystemClock.uptimeMillis() - start));
        } else {
            bitmap = BitmapTool.createBitmap(bytesBuffer, mImageRequest.getDecodeConfig());
        }

        // orientation correction
        long exifTime = SystemClock.uptimeMillis();
        if (LogUtils.DEBUG) LogUtils.d(TAG, "save => exif rotate");
        int orientation = ExifTool.getOrientation(bytesBuffer);
        if (LogUtils.DEBUG) LogUtils.d(TAG, "save: orientation=" + orientation);
        bitmap = BitmapTool.rotateBitmap(bitmap, orientation);
        if (LogUtils.DEBUG) LogUtils.d(TAG, "save <= exif rotate, time consumed=" + (SystemClock.uptimeMillis() - exifTime));

        // post-processes
        if (mImageRequest.getImageType() == ImageType.ROUND) {
            long start = SystemClock.uptimeMillis();
            if (LogUtils.DEBUG) LogUtils.d(TAG, "save => round()");
            bitmap = BitmapTool.toRoundBitmap(bitmap, mImageRequest.getRadius());
            if (LogUtils.DEBUG) LogUtils.d(TAG, "save <= round(), time consumed=" + (SystemClock.uptimeMillis() - start));
        }
    /*       
     * TV6.0 is no need to clip corners for bitmap
     * 
     *  if (mImageRequest.shouldRoundCorner()) {
            long start = SystemClock.uptimeMillis();
            if (LogUtils.DEBUG) LogUtils.d(TAG, "save => roundCorner()");
            bitmap = BitmapTool.clipToRoundCorners(bitmap, mImageRequest.getRoundCornerSpecs());
            if (LogUtils.DEBUG) LogUtils.d(TAG, "save <= roundCorner(), time consumed=" + (SystemClock.uptimeMillis() - start));
        }*/
        
        if (LogUtils.DEBUG) LogUtils.d(TAG, "save: post-processed bitmap=" + LogUtils.printBitmap(bitmap));
        if (bitmap != null) {
            final SoftReference<Bitmap> weakBitmap = new SoftReference<Bitmap>(bitmap);
            
        	String savePath = null;
            long l = SystemClock.uptimeMillis();
            if (LogUtils.DEBUG) LogUtils.d(TAG, "saveAsync() begin.");
            boolean directSave = mImageRequest.getImageType() == ImageType.DEFAULT
                    && !ImageProvider.get().shouldScale(mImageRequest)
                    && ImageProvider.get().isEnableFastSave()
                    && mImageRequest.getScaleType() != ScaleType.CENTER_INSIDE;
            if (LogUtils.DEBUG) LogUtils.d(TAG, "saveAsync: directSave=" + directSave);
            if (!directSave) {
                boolean recycled = true;
                Bitmap savedbitmap = weakBitmap.get();
                if (savedbitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    savedbitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                            InputStream isBm = new ByteArrayInputStream(baos .toByteArray());
//                            savedbitmap = BitmapFactory.decodeStream(isBm);
                    savePath = mCache.recordFile(mImageRequest, baos.toByteArray());
                    recycled = false;
                }
                if (LogUtils.DEBUG) LogUtils.d(TAG, "saveAsync(): recycled=" + recycled);
            } else {
            	savePath = mCache.recordFile(mImageRequest, bytesBuffer);
            }
            if (!StringUtils.isEmpty(savePath)) {
            	notifyUISuccess(savePath);
            	long n = SystemClock.uptimeMillis();
            	if (LogUtils.DEBUG) LogUtils.d(TAG, "saveAsync() end consume:" + (n - l));
            	LogUtils.d(TAG, "<< save(), total time consumed=" + (SystemClock.uptimeMillis() - saveStart));
            	return true;
			} else {
				return false;
			}
        } else {
            LogUtils.d(TAG, "<< save() fail, total time consumed="
                    + (SystemClock.uptimeMillis() - saveStart));
            return false;
        }
    }

    /**
     * retry download, if possible.
     * 
     * @return true if retry will be performed
     */
    private boolean callRetry() {
        if (mIsDeprecated) {
            return false;
        }
        if (mRetryCounter < getTotalRetryCount()) {
            mRetryCounter++;
            onRetryDownload();
            return true;
        } else {
            LogUtils.d(TAG, "callRetry: limit reached, failed url=" + mImageRequest.getUrl());
            notifyUIFailure();
            return false;
        }
    }

    /**
     * set try timeout
     */
    protected void onRetryDownload() {
        switch (mRetryCounter) {
        case 1:
            mConnectTimeout = getRetryConnTimeout();
            mReadTimeout = getRetryReadTimeout();
            break;
        case 2:
            mConnectTimeout = getRetryConnTimeout();
            mReadTimeout = getRetryReadTimeout() * 2;
            break;
        }
        LogUtils.d(TAG, "onRetryDownload: url=" + mImageRequest.getUrl());
        ImageProvider.get().addTask(this);
    }

    protected void notifyUISuccess(String saveUrl) {
        if (!mIsDeprecated) {
            success(saveUrl);
        }
    }

    protected void notifyUIFailure() {
        if (!mIsDeprecated) {
            failure(new USAException(USAException.REQUEST_FAIL));
        }
    }

    public void failure(Exception ex) {
    	mCocosCallback.onFailure(getImageRequest(), ex);
		getImageRequest().getSameTaskQueue().failure(ex);
    }

    public void success(String saveUrl) {
		mCocosCallback.onSuccess(getImageRequest(), saveUrl);
		getImageRequest().getSameTaskQueue().success(saveUrl);
    }
}