package com.qiyi.imageprovider.logic;

import java.util.List;

import com.qiyi.imageprovider.base.IFileCallback;
import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.cocos2d.CocosHttpTask;
import com.qiyi.imageprovider.cocos2d.ICocosCallback;
import com.qiyi.imageprovider.cocos2d.LastCocosHttpTask;
import com.qiyi.imageprovider.logic.task.NormalHttpTask;
import com.qiyi.imageprovider.logic.task.OnlyFileHttpTask;
import com.qiyi.imageprovider.logic.task.HttpTask;
import com.qiyi.imageprovider.logic.task.LastHttpTask;
import com.qiyi.imageprovider.logic.task.OnlyWebHttpTask;
import com.qiyi.imageprovider.logic.task.PriorityHttpTask;
import com.qiyi.imageprovider.model.USAException;
import com.qiyi.imageprovider.model.WaitingQueue;
import com.qiyi.imageprovider.pingback.ImagePingback;
import com.qiyi.imageprovider.util.LogUtils;

public class TaskLoader extends ThreadPool {
    private static final String TAG = "ImageProvider/TaskLoader";
    private boolean mIsPingbackPermit = true;
	/**
	 * download tasks
	 * @param imageRequestList
	 * @param imageCallback
	 */
	protected void downloadBitmap(final List<ImageRequest> imageRequestList, 
			final IImageCallback imageCallback) {
		if (!ParamsUtils.isCorrectParams(imageRequestList, imageCallback)) {
			return;
		}
		
		for (ImageRequest imageRequest : imageRequestList) {
			if (addRepeatTask(mWaitingQueue, 
					imageRequest, imageCallback)) {
			    if (LogUtils.DEBUG) LogUtils.d(TAG, "addRepeatTask() add " + imageRequest);
				continue;
			}
			HttpTask runnableTask;
			if (imageRequest.isLasting()) {
				runnableTask = new LastHttpTask(imageRequest, ImagePingback.instance(mIsPingbackPermit), imageCallback);
			} else {
				runnableTask = new NormalHttpTask(imageRequest, ImagePingback.instance(mIsPingbackPermit), imageCallback);
			}
			if (runnableTask != null) {
				mThreadPoolExecutor.execute(runnableTask);
			}
			if (LogUtils.DEBUG) LogUtils.d(TAG, "downloadBitmap() add " + imageRequest + ", runnableTask=" + runnableTask);
		}
	}
	
	/**
	 * download tasks Cocos2d-x
	 * @param imageRequestList
	 * @param cocosCallback
	 */
	protected void downloadBitmap(final List<ImageRequest> imageRequestList, final ICocosCallback cocosCallback) {
		if(cocosCallback == null){
			return;
		}
		if(imageRequestList == null || imageRequestList.size() == 0){
			cocosCallback.onFailure(null, 
					new USAException(USAException.PARAMS_ERROR));
			return;
		}
		
		for (ImageRequest imageRequest : imageRequestList) {
			if (addRepeatTask(mWaitingQueue, imageRequest, cocosCallback)) {
				if (LogUtils.DEBUG) LogUtils.d(TAG, "addRepeatTask() add " + imageRequest);
				continue;
			}
			CocosHttpTask runnableTask;
			if (imageRequest.isLasting()) {
				runnableTask = new LastCocosHttpTask(imageRequest, ImagePingback.instance(mIsPingbackPermit), cocosCallback);
			} else {
				runnableTask = new CocosHttpTask(imageRequest, ImagePingback.instance(mIsPingbackPermit), cocosCallback);
			}
			if (runnableTask != null) {
				mThreadPoolExecutor.execute(runnableTask);
			}
			if (LogUtils.DEBUG) LogUtils.d(TAG, "downloadBitmap() add " + imageRequest + ", runnableTask=" + runnableTask);
		}
	}
	
	/**
	 * download files
	 * @param imageRequestList
	 * @param imageCallback
	 */
	protected void downloadFiles(final List<ImageRequest> imageRequestList, 
			final IFileCallback fileCallback) {
		if (!ParamsUtils.isCorrectParams(imageRequestList, fileCallback)) {
			return;
		}
		
		for (ImageRequest imageRequest : imageRequestList) {
			if (addRepeatTask(mWaitingQueue, imageRequest, fileCallback)) {
				continue;
			}
			HttpTask runnableTask = 
					new OnlyFileHttpTask(imageRequest, ImagePingback.instance(mIsPingbackPermit), fileCallback);
			if (runnableTask != null) {
				mThreadPoolExecutor.execute(runnableTask);
			}
		}
	}
	
	/**
	 * download from web
	 * @param imageRequest
	 * @param imageCallback
	 */
	protected void downloadBitmap4Web(final ImageRequest imageRequest, 
			final IImageCallback imageCallback) {
		if (!ParamsUtils.isCorrectParams(imageRequest, imageCallback)) {
			return;
		}
		
		if (addRepeatTask(mWaitingQueue, imageRequest, imageCallback)) {
			return;
		}
		HttpTask runnableTask = new 
				OnlyWebHttpTask(imageRequest, ImagePingback.instance(mIsPingbackPermit), imageCallback);
		if (runnableTask != null) {
			mThreadPoolExecutor.execute(runnableTask);
		}
	}
	
	protected void downloadPriorityTask(ImageRequest imageRequest,
			IImageCallback imageCallback) {
		if (!ParamsUtils.isCorrectParams(imageRequest, imageCallback)) {
			return ;
		}
		
		HttpTask runnableTask = 
				new PriorityHttpTask(imageRequest, ImagePingback.instance(mIsPingbackPermit), imageCallback);
		if (runnableTask != null) {
			(new Thread(runnableTask)).start();
		}
	}
	
	protected void addTask(HttpTask runnableTask) {
		mThreadPoolExecutor.execute(runnableTask);
	}
	
	protected void addTask(CocosHttpTask runnableTask) {
		mThreadPoolExecutor.execute(runnableTask);
	}
	
	protected void addSaveTask(Runnable runnableTask) {
        mThreadPoolExecutor.execute(runnableTask);
    }
	
	protected void addRunningTask(Runnable runnable) {
		mRunningQueue.add(runnable);
	}
	
	protected void removeRunningTask(Runnable runnable) {
		mRunningQueue.remove(runnable);
	}
	
	protected void cancelTask(String url) {
		mWaitingQueue.remove(url);
	}
	
	protected void cancelTask(ImageRequest request) {
		mWaitingQueue.remove(request);
	}
	
	protected void cancelAllTasks() {
		if(mWaitingQueue.clear()){
			mRunningQueue.shutdown(mThreadPoolExecutor);
		}
	}
	
	/**
	 * identical
	 * @param imageRequest
	 * @param imageCallback
	 * @return
	 */
	private boolean addRepeatTask(WaitingQueue waitingQueue, 
			ImageRequest imageRequest, IImageCallback imageCallback) {
		ImageRequest baseRequest = getSameTask(waitingQueue, imageRequest);
		if (LogUtils.DEBUG) LogUtils.d(TAG, "addRepeatTask: baseRequest=" + baseRequest);
		if (baseRequest != null) {
			baseRequest.getSameTaskQueue().add(imageRequest, imageCallback);
			return true;
		}
		return false;
	}
	
	private boolean addRepeatTask(WaitingQueue waitingQueue, 
			ImageRequest imageRequest, IFileCallback fileCallback) {
		ImageRequest baseRequest = getSameTask(waitingQueue, imageRequest);
		if (baseRequest != null) {
			baseRequest.getSameTaskQueue().add(imageRequest, fileCallback);
			return true;
		}
		return false;
	}
	
	private boolean addRepeatTask(WaitingQueue waitingQueue, 
			ImageRequest imageRequest, ICocosCallback cocosCallback) {
		ImageRequest baseRequest = getSameTask(waitingQueue, imageRequest);
		if (LogUtils.DEBUG) LogUtils.d(TAG, "addRepeatTask: baseRequest=" + baseRequest);
		if (baseRequest != null) {
			baseRequest.getSameTaskQueue().add(imageRequest, cocosCallback);
			return true;
		}
		return false;
	}
	
	private ImageRequest getSameTask(WaitingQueue waitingQueue, 
			ImageRequest imageRequest) {
		return waitingQueue.getSameTask(imageRequest);
	}

	public boolean isPingbackPermit() {
		return mIsPingbackPermit;
	}

	public void setIsPingbackPermit(boolean mIsPingbackPermit) {
		this.mIsPingbackPermit = mIsPingbackPermit;
	}
	
}
