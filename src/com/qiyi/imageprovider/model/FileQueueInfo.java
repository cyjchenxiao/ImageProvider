package com.qiyi.imageprovider.model;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.IFileCallback;
import com.qiyi.imageprovider.base.ImageRequest;

public class FileQueueInfo extends QueueInfo {

	private IFileCallback mFileCallback;
	
	public FileQueueInfo(ImageRequest imageRequest, IFileCallback callback) {
		super(imageRequest);
		mFileCallback = callback;
	}
	
	@Override
	public void notifyUISuccess(Bitmap bitmap, String saveUrl) {
		mFileCallback.onSuccess(getRequest(), saveUrl);
	}

	@Override
	public void notifyUIFailure(Exception ex) {
		mFileCallback.onFailure(getRequest(), ex);
	}
	
}
