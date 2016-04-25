package com.qiyi.imageprovider.model;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;

public class BitmapQueueInfo extends QueueInfo {

	private IImageCallback mImageCallback;
	
	public BitmapQueueInfo(ImageRequest imageRequest, IImageCallback callback) {
		super(imageRequest);
		mImageCallback = callback;
	}
	
	@Override
	public void notifyUISuccess(Bitmap bitmap, String saveUrl) {
		mImageCallback.onSuccess(getRequest(), bitmap);
	}

	@Override
	public void notifyUIFailure(Exception ex) {
		mImageCallback.onFailure(getRequest(), ex);
	}
	
}
