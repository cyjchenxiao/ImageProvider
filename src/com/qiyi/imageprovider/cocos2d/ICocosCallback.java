package com.qiyi.imageprovider.cocos2d;

import com.qiyi.imageprovider.base.ImageRequest;

public interface ICocosCallback {
	/**
	 * response success
	 * @param imageRequest
	 * @param filePath
	 */
	public void onSuccess(ImageRequest imageRequest, String filePath);
	
	/**
	 * response fail
	 * @param imageRequest
	 * @param ex
	 */
	public void onFailure(ImageRequest imageRequest, Exception ex);
}
