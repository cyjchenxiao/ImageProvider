package com.qiyi.imageprovider.logic;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;

import com.qiyi.imageprovider.logic.task.HttpTask;
import com.qiyi.imageprovider.model.RunningQueue;
import com.qiyi.imageprovider.model.USAException;
import com.qiyi.imageprovider.model.WaitingQueue;
import com.qiyi.imageprovider.util.DebugOptions;
import com.qiyi.imageprovider.util.LogUtils;

class ThreadPool {
    private static final String TAG = "ImageProvider/ThreadPool";

    // maintain the number of threads
    private int mCorePoolSize = 2;
    // maintain the maximum number of threads
    private int mMaximumPoolSize = 2;
    // seconds
    private long mKeepAliveTime = 5;
    // the number of the maintaining task
    private int WAITING_TASK_SIZE = 200;
    private int mThreadPriority = Process.THREAD_PRIORITY_LOWEST;

    protected ThreadPoolExecutor mThreadPoolExecutor;

    protected RunningQueue mRunningQueue = new RunningQueue();
    protected WaitingQueue mWaitingQueue = new WaitingQueue(WAITING_TASK_SIZE);
    
    private AtomicInteger mIndex = new AtomicInteger(0);

    ThreadPool() {
        createThreadPool();
    }

    /**
     * open Smoothmode (suitable for continuous loading)
     * 
     * @param cpuCoreCount
     */
    protected void openSmoothmode(int cpuCoreCount) {
        if (cpuCoreCount >= 2) {
            mCorePoolSize = cpuCoreCount;
            mMaximumPoolSize = cpuCoreCount;
            destroyThreadPool();
            createThreadPool();
        }
        // //no use
        // switch(cpuCoreCount) {
        // case 2:
        // case 4:
        // mCorePoolSize = 3;
        // mMaximumPoolSize = 3;
        // destroyThreadPool();
        // createThreadPool();
        // break;
        // default:
        // break;
        // }
    }

    /**
     * close Smoothmode
     */
    protected void closeSmoothmode() {
        if (mCorePoolSize != 2) {
            mCorePoolSize = 2;
            mMaximumPoolSize = 2;
            destroyThreadPool();
            createThreadPool();
        }
    }

    public void setThreadPriority(int priority) {
        mThreadPriority = priority;
        destroyThreadPool();
        createThreadPool();
    }

    /**
     * create threadpool
     */
    private void createThreadPool() {
        if (mThreadPoolExecutor == null) {
            mThreadPoolExecutor = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime,
                    TimeUnit.SECONDS, mWaitingQueue.getWaitingQueue(), mDiscardOldestPolicy);
            setThreadPriority(mThreadPoolExecutor);
        }
    }

    private void setThreadPriority(ThreadPoolExecutor poolExecutor) {
        final boolean isLowPriority = DebugOptions.isLowDecodePriority();
        poolExecutor.setThreadFactory(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable arg0) {
                String name = "ImageProvider-" + mIndex.getAndIncrement();
                Thread t = new Thread(arg0, name) {
                    @Override
                    public void run() {
                        if (isLowPriority) {
                            Process.setThreadPriority(mThreadPriority);
                        }
                        LogUtils.d(TAG, "thread priority setting is " + (isLowPriority ? "LOW" : "HIGH")
                                + ", value=" + Thread.currentThread().getPriority() + "(java)/"
                                + Process.getThreadPriority(Process.myTid()) + "(android)");
                        super.run();
                    }
                };
                if (t.isDaemon()) {
                    t.setDaemon(false);
                }
                return t;
            }
        });
    }

    private void destroyThreadPool() {
        if (mThreadPoolExecutor != null) {
            List<Runnable> runnables = mThreadPoolExecutor.shutdownNow();
            for (Runnable r : runnables) {
                if (r instanceof HttpTask) {
                    HttpTask task = (HttpTask) r;
                    task.failure(new USAException(USAException.TASK_CANCEL));
                }
            }
            mThreadPoolExecutor = null;
        }
    }

    ThreadPoolExecutor.DiscardOldestPolicy mDiscardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (r instanceof HttpTask) {
                HttpTask runnableTask = (HttpTask) r;
                runnableTask.failure(new USAException(USAException.COUNT_TOO_MUCH));
            }
        }
    };

}
