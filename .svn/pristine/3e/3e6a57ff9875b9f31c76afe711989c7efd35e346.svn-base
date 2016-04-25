package com.qiyi.imageprovider.logic.task;

import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;

public class LastHttpTask extends NormalHttpTask {
	private static final long serialVersionUID = 1L;
	
	public LastHttpTask(ImageRequest imageRequest, ITaskStatusListener listener, IImageCallback imageCallback) {
		super(imageRequest, listener, imageCallback);
	}
	
	@Override
	protected int getConnTimeout() {
		return 5000;
	}

	@Override
	protected int getReadTimeout() {
		return 10000;
	}

}
