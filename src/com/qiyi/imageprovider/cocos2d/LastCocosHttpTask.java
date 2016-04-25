package com.qiyi.imageprovider.cocos2d;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.logic.task.ITaskStatusListener;

public class LastCocosHttpTask extends CocosHttpTask {
	private static final long serialVersionUID = 1L;
	
	public LastCocosHttpTask(ImageRequest imageRequest, ITaskStatusListener listener, ICocosCallback imageCallback) {
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
