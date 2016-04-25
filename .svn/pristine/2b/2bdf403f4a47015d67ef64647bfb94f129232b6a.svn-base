package com.qiyi.imageprovider.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.qiyi.imageprovider.logic.task.HttpTask;

public class RunningQueue {
	private List<Runnable> mRunningTaskQueue = new ArrayList<Runnable>();
	
	public synchronized void add(Runnable runnable) {
		mRunningTaskQueue.add(runnable);
	}
	
	public synchronized void remove(Runnable runnable) {
		if (mRunningTaskQueue.contains(runnable)) {
			mRunningTaskQueue.remove(runnable);
		}
	}
	
	public synchronized void shutdown(ThreadPoolExecutor threadPoolExecutor) {
		for (Runnable runnable : mRunningTaskQueue) {
		    if (runnable instanceof HttpTask) {
    			HttpTask task = (HttpTask) runnable;
    			if (task != null) {
    				task.deprecate();
    			}
		    }
			threadPoolExecutor.remove(runnable);
		}
		mRunningTaskQueue.clear();
	}
}
