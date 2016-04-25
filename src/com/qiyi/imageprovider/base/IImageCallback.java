package com.qiyi.imageprovider.base;

import android.graphics.Bitmap;

public interface IImageCallback {
	/**
	 * response success
	 * @param imageRequest
	 * @param bitmap
	 */
	public void onSuccess(ImageRequest imageRequest, Bitmap bitmap);
	
	/**
	 * response fail
	 * @param imageRequest
	 * @param ex
	 */
	public void onFailure(ImageRequest imageRequest, Exception ex);
}
