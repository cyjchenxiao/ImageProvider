package com.qiyi.imageprovider.logic.task;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;

public class PriorityHttpTask extends HttpTask {
	private static final long serialVersionUID = 1L;

	private IImageCallback mImageCallback;
	
	public PriorityHttpTask(ImageRequest imageRequest, ITaskStatusListener listener, IImageCallback imageCallback) {
		super(imageRequest, listener);
		mImageCallback = imageCallback;
	}
	
	@Override
	protected int getConnTimeout() {
		return 10000;
	}

	@Override
	protected int getReadTimeout() {
		return 30000;
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
	
	@Override
	protected void onRetryDownload() {
		run();
	}

}
