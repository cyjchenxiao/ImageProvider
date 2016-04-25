package com.qiyi.imageprovider.model;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.logic.task.HttpTask;
import com.qiyi.imageprovider.util.LogUtils;

public class WaitingQueue {
	private ArrayBlockingQueue<Runnable> mArrayBlockingQueue;
	
	public WaitingQueue(int size) {
		mArrayBlockingQueue = new ArrayBlockingQueue<Runnable>(size);
	}
	
	public ArrayBlockingQueue<Runnable> getWaitingQueue() {
		return mArrayBlockingQueue;
	}
	
	public synchronized ImageRequest getSameTask(String url) {
		for (Runnable r : mArrayBlockingQueue) {
		    if (r instanceof HttpTask) {
    			HttpTask task = (HttpTask) r;
    			String taskUrl = task.mImageRequest.getUrl();
    			if (taskUrl != null && taskUrl.equals(url)) {
    				return task.mImageRequest;
    			}
		    }
		}
		return null;
	}
	
	public synchronized ImageRequest getSameTask(ImageRequest request) {
        for (Runnable r : mArrayBlockingQueue) {
            if (r instanceof HttpTask) {
                HttpTask task = (HttpTask) r;
                ImageRequest taskReq = task.mImageRequest;
                if (taskReq != null && taskReq.equals(request)) {
                    return task.mImageRequest;
                }
            }
        }
        return null;
    }
	
	public synchronized boolean clear() {
//		mArrayBlockingQueue.clear();
		if(LogUtils.DEBUG)LogUtils.e("ImageRequest", "before clear tasks count : " + mArrayBlockingQueue.size());
		boolean flag = true;
		
		Iterator<Runnable> iterator = mArrayBlockingQueue.iterator();
		while (iterator.hasNext()) {
			Runnable runnable = iterator.next();
			if (runnable instanceof HttpTask) {
				HttpTask task = (HttpTask) runnable;
				ImageRequest taskReq = task.mImageRequest;
				if (taskReq != null && taskReq.getShouldBeKilled()) {
					iterator.remove();
				} else {
					flag = false;
					if(LogUtils.DEBUG)LogUtils.e("ImageRequest", "clear ---- keep alive : " + taskReq.getUrl());
				}
			}
		}
		if(LogUtils.DEBUG)LogUtils.e("ImageRequest", "after clear tasks count : " + mArrayBlockingQueue.size());
		return flag;
	}
	
	public synchronized boolean remove(String url) {
		for (Runnable r : this.mArrayBlockingQueue) {
		    if (r instanceof HttpTask) {
    			HttpTask task = (HttpTask) r;
    			if (task.isEquals(url)) {
    				mArrayBlockingQueue.remove(task);
    				return true;
    			}
		    }
		}
		return false;
	}
	
	public synchronized boolean remove(ImageRequest reqest) {
		for (Runnable r : this.mArrayBlockingQueue) {
		    if (r instanceof HttpTask) {
    			HttpTask task = (HttpTask) r;
    			if (task.isEquals(reqest)) {
    				mArrayBlockingQueue.remove(task);
    				return true;
    			}
		    }
		}
		return false;
	}
	
}
