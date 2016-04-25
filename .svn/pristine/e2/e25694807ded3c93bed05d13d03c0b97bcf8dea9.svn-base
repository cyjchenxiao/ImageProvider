package com.qiyi.imageprovider.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.IFileCallback;
import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.cocos2d.CocosQueueInfo;
import com.qiyi.imageprovider.cocos2d.ICocosCallback;

public class RequestQueue {

	private List<QueueInfo> mTaskQueue = new ArrayList<QueueInfo>();
	
	public void add(ImageRequest imageRequest, IImageCallback callback) {
		QueueInfo info = new BitmapQueueInfo(imageRequest, callback);
		mTaskQueue.add(info);
	}
	
	public void add(ImageRequest imageRequest, ICocosCallback callback) {
		QueueInfo info = new CocosQueueInfo(imageRequest, callback);
		mTaskQueue.add(info);
	}
	
	public void add(ImageRequest imageRequest, IFileCallback callback) {
		QueueInfo info = new FileQueueInfo(imageRequest, callback);
		mTaskQueue.add(info);
	}
	
	public void success(Bitmap bitmap) {
		success(bitmap, null);
	}
	
	public void success(String saveUrl) {
		success(null, saveUrl);
	}
	
	private void success(Bitmap bitmap, String saveUrl) {
		synchronized (mTaskQueue) {
			for (QueueInfo info : mTaskQueue) {
				if(info instanceof BitmapQueueInfo) {
					BitmapQueueInfo bitInfo = (BitmapQueueInfo) info;
					bitInfo.notifyUISuccess(bitmap, saveUrl);
				} else if(info instanceof FileQueueInfo) {
					FileQueueInfo fileInfo = (FileQueueInfo) info;
					fileInfo.notifyUISuccess(bitmap, saveUrl);
				}
			}
			mTaskQueue.clear();
		}
	}
	
	public void failure(Exception ex) {
		synchronized (mTaskQueue) {
			for (QueueInfo info : mTaskQueue) {
				if(info instanceof BitmapQueueInfo) {
					BitmapQueueInfo bitInfo = (BitmapQueueInfo) info;
					bitInfo.notifyUIFailure(ex);
				} else if(info instanceof FileQueueInfo) {
					FileQueueInfo fileInfo = (FileQueueInfo) info;
					fileInfo.notifyUIFailure(ex);
				}
			}
			mTaskQueue.clear();
		}
	}

}
