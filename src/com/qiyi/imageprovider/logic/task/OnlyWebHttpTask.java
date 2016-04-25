package com.qiyi.imageprovider.logic.task;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.base.ImageRequest.ImageType;
import com.qiyi.imageprovider.util.BitmapTool;
import com.qiyi.imageprovider.util.LogUtils;

public class OnlyWebHttpTask extends HttpTask {
    private static final String TAG = "ImageProvider/OnlyWebHttpTask";
	private static final long serialVersionUID = 1L;
	
	private IImageCallback mImageCallback;
	
	public OnlyWebHttpTask(ImageRequest imageRequest, ITaskStatusListener listener, IImageCallback allback) {
		super(imageRequest, listener);
		mImageCallback = allback;
	}
	
	@Override
	protected boolean checkMemory(ImageRequest request) {
		return false;
	}

	@Override
	protected boolean checkFile(ImageRequest request) {
		return false;
	}
	
	protected boolean save(String url, byte[] bytesBuffer) {
	    if (LogUtils.DEBUG) LogUtils.d(TAG, "save(" + url + ") begin");
	    Bitmap bitmap = null;
        ImageType type = mImageRequest.getImageType();
        if(type == ImageType.RECT) {
            bitmap = BitmapTool.createBitmap(bytesBuffer);
        } else if (type == ImageType.ROUND) {
            bitmap = BitmapTool.toRoundForce(bytesBuffer, mImageRequest.getRadius());
        } else {
            bitmap = BitmapTool.toDefaultBitmap(bytesBuffer);
        }
        if (LogUtils.DEBUG) LogUtils.d(TAG, "save(" + url + ") " + type + ", bitmap=" + bitmap);
		if(bitmap != null) {
			notifyUISuccess(bitmap);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void success(Bitmap bitmap) {
		mImageCallback.onSuccess(getImageRequest(), bitmap);
		getImageRequest().getSameTaskQueue().success(bitmap);
	}
	
	@Override
	public void failure(Exception ex) {
		mImageCallback.onFailure(getImageRequest(), ex);
		getImageRequest().getSameTaskQueue().failure(ex);
	}

}
