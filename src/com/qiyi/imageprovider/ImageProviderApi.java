package com.qiyi.imageprovider;

import com.qiyi.imageprovider.base.IImageProvider;
import com.qiyi.imageprovider.logic.ImageProvider;

public class ImageProviderApi {
	/**
	 * get ImageProvider
	 * @return
	 */
	public static IImageProvider getImageProvider() {
		return ImageProvider.get();
	}
	
	public static void setChacheFile(boolean isCache) {
		ImageProvider.get().setCacheFile(isCache);
	}
}
