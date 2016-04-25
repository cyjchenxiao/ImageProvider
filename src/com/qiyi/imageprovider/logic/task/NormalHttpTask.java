package com.qiyi.imageprovider.logic.task;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;

public class NormalHttpTask extends HttpTask {
	private static final long serialVersionUID = 1L;
	
	private IImageCallback mImageCallback;
	
	public NormalHttpTask(ImageRequest imageRequest, ITaskStatusListener listener, 
			IImageCallback imageCallback) {
		super(imageRequest, listener);
		mImageCallback = imageCallback;
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
