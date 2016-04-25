package com.qiyi.imageprovider.pingback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Handler;
import android.os.HandlerThread;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.logic.task.ITaskStatusListener;
import com.qiyi.imageprovider.util.LogUtils;
import com.qiyi.video.qiyipingback2.QiyiPingBack2;

public class ImagePingback implements ITaskStatusListener{
    private static final String TAG = "ImageProvider/ImagePingback";
    private static ImagePingback sInstance;
    private static Handler mHandler;
    private boolean mIsPermitFlag;
    
    private ImagePingback(boolean permit) { 
    	mIsPermitFlag = permit;
        HandlerThread handlerThread  = new HandlerThread("imagePingback");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

	public static synchronized ImagePingback instance(boolean permit) {
        if (sInstance == null) {
            sInstance = new ImagePingback(permit);
        }
        return sInstance;
    }
    
    static class PingbackRunnable implements Runnable {
        String mUrl;
        String mExpName;
        String mHttpCode;

        public PingbackRunnable(String url, String expName, String httpCode) {
            mUrl = url;
            mExpName = expName;
            mHttpCode = httpCode;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                if (LogUtils.DEBUG) LogUtils.d(TAG, "PingbackRunnable.run", e);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US);
            String clientTime = format.format(new Date());
            
            QiyiPingBack2.get().downloadPicFail(clientTime, mUrl, mExpName, mHttpCode, "", "", "", "");
            if (LogUtils.DEBUG) LogUtils.d(TAG, "PingbackRunnable.run: sent, client time=" + clientTime
                        + ", url=" + mUrl + ", expName=" + mExpName + ", httpCode=" + mHttpCode);
        }

    }
    
    
    @Override
    public void onHttpRequestFailed(ImageRequest request, Throwable exception, String httpCode) {
        if (LogUtils.DEBUG) LogUtils.d(TAG, ">> onHttpRequestFailed: request={" + request + "}, exception=" + exception + ", httpCode=" + httpCode);
        String expName = exception != null ? exception.getClass().getName() : "";
        if (mIsPermitFlag) {
        	mHandler.post(new PingbackRunnable(request.getUrl(), expName, httpCode));
		} else {
			LogUtils.d(TAG, ">>>>> onHttpRequestFailed ---- pingback is forbid");
		}
    }

    @Override
    public void onImageLoadTimedOut() { }

}
