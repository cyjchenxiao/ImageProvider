package com.qiyi.imageprovider.logic.task;

import com.qiyi.imageprovider.base.IFileCallback;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.logic.cache.Cache;

public class OnlyFileHttpTask extends HttpTask {

	private static final long serialVersionUID = 1L;
	
	private IFileCallback mFileCallback;
	
	public OnlyFileHttpTask(ImageRequest imageRequest, ITaskStatusListener listener, IFileCallback fileCallback) {
		super(imageRequest, listener);
		mFileCallback = fileCallback;
	}
	
	@Override
	protected boolean checkMemory(ImageRequest request) {
		return false;
	}

	@Override
	protected boolean checkFile(ImageRequest request) {
		String path = Cache.get().getFileRealPath(request);
		if (path != null) {
			notifyUISuccess(path);
			return true;
		}
		return false;
	}
	
	protected boolean save(String url, byte[] bytesBuffer) {
		String savePath = Cache.get().recordFileMust(mImageRequest, bytesBuffer);
		if(savePath != null) {
			notifyUISuccess(savePath);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void success(String saveUrl) {
		mFileCallback.onSuccess(getImageRequest(), saveUrl);
		getImageRequest().getSameTaskQueue().success(saveUrl);
	}
	
	@Override
	public void failure(Exception ex) {
		mFileCallback.onFailure(getImageRequest(), ex);
		getImageRequest().getSameTaskQueue().failure(ex);
	}
	
}
