package com.qiyi.imageprovider.cocos2d;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.model.QueueInfo;

public class CocosQueueInfo extends QueueInfo {

	private ICocosCallback mCocosCallback;
	
	public CocosQueueInfo(ImageRequest imageRequest, ICocosCallback callback) {
		super(imageRequest);
		mCocosCallback = callback;
	}
	
	@Override
	public void notifyUISuccess(Bitmap bitmap, String saveUrl) {
		mCocosCallback.onSuccess(getRequest(), saveUrl);
	}

	@Override
	public void notifyUIFailure(Exception ex) {
		mCocosCallback.onFailure(getRequest(), ex);
	}
	
}
