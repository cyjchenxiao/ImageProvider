package com.qiyi.imageprovider.logic.task;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.logic.cache.Cache;
import com.qiyi.imageprovider.util.BitmapTool;
import com.qiyi.imageprovider.util.LogUtils;

public class NoMemoryHttpTask extends HttpTask {
    private static final String TAG = "ImageProvider/NoMemoryHttpTask";
    
	private static final long serialVersionUID = 1L;
	
	private IImageCallback mImageCallback;
	
	public NoMemoryHttpTask(ImageRequest imageRequest, ITaskStatusListener listener, IImageCallback imageCallback) {
		super(imageRequest, listener);
		mImageCallback = imageCallback;
	}
	
	@Override
	protected boolean checkMemory(ImageRequest request) {
		return false;
	}
	
	@Override
	protected boolean checkFile(ImageRequest request) {
	    if (LogUtils.DEBUG) LogUtils.d(TAG, "checkFile(" + request.getUrl() + ") begin");
		Bitmap bitmap = Cache.get().getFileBitmap(request, false);
		if (LogUtils.DEBUG) LogUtils.d(TAG, "checkFile(" + request.getUrl() + ") " + bitmap);
		if (bitmap != null) {
			notifyUISuccess(bitmap);
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean save(String url, byte[] bytesBuffer) {
	    if (LogUtils.DEBUG) LogUtils.d(TAG, "save(" + url + ") begin");
		Bitmap bitmap = BitmapTool.createBitmap(bytesBuffer);
		if (LogUtils.DEBUG) LogUtils.d(TAG, "save(" + url + ") scale " + bitmap);
		if(bitmap != null) {
			notifyUISuccess(bitmap);
			Cache.get().recordFile(mImageRequest, bytesBuffer);
			if (LogUtils.DEBUG) LogUtils.d(TAG, "save(" + url + ") end");
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
