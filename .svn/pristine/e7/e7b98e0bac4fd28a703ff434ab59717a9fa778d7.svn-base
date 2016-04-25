package com.qiyi.imageprovider.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.Config;

import com.qiyi.imageprovider.base.IFileCallback;
import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.IImageProvider;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.base.ImageRequest.ScaleType;
import com.qiyi.imageprovider.cocos2d.CocosHttpTask;
import com.qiyi.imageprovider.cocos2d.ICocosCallback;
import com.qiyi.imageprovider.logic.cache.Cache;
import com.qiyi.imageprovider.logic.task.HttpTask;
import com.qiyi.imageprovider.model.USAException;
import com.qiyi.imageprovider.util.ContextUtils;
import com.qiyi.imageprovider.util.DebugOptions;
import com.qiyi.imageprovider.util.LogUtils;

public class ImageProvider implements IImageProvider {
    private static final String TAG = "ImageProvider/ImageProvider";
	static { 
		Cache.get(); 
	}
	private static ImageProvider gImageProvider;
	
	private Cache mCache = Cache.get();
	private TaskLoader mTaskDock = new TaskLoader();
	private boolean mEnableScale = false;
	private boolean mEnableFastSave = true;
	private boolean mEnableFullPathCacheKey;
	private Config mDecodeConfig;
	
	public static synchronized ImageProvider get() {
	    if (gImageProvider == null) {
	        gImageProvider = new ImageProvider();
	    }
		return gImageProvider;
	}
	
	private ImageProvider() { }
	
	@Override
    public void initialize(Context context) {
	    ContextUtils.instance().initialize(context);
        mCache.setContext(context);
        DebugOptions.reset();
    }
	
	@Override
	public void initialize(Context context, String defaultUserId) {
	    ContextUtils.instance().initialize(context);
        mCache.setContext(context);
        DebugOptions.reset();
	}
	
	public void setCacheFile(boolean isCache) {
		mCache.setWriteFile(isCache);
	}
	
	@Override
	public void loadImage(ImageRequest imageRequest, IImageCallback imageCallback) {
	    if (!ImageRequest.checkRequestValid(imageRequest)) {
            LogUtils.e(TAG, "loadImage: invalid request: " + imageRequest);
            if (imageCallback != null) {
                imageCallback.onFailure(imageRequest, new USAException(USAException.PARAMS_ERROR));
            }
            return;
        }
	    if (LogUtils.DEBUG) LogUtils.d(TAG, "loadImage: request=" + imageRequest + ", callback=" + imageCallback);
		List<ImageRequest> list = new ArrayList<ImageRequest>();
		list.add(imageRequest);
		loadImages(list, imageCallback);
	}
	
	@Override
	public void loadImages(List<ImageRequest> imageRequestList, 
			IImageCallback imageCallback) {
	    updateRequest(imageRequestList);
		mTaskDock.downloadBitmap(imageRequestList, imageCallback);
	}
	
	@Override
	public void loadImage(ImageRequest imageRequest, ICocosCallback imageCallback) {
		if (!ImageRequest.checkRequestValid(imageRequest)) {
			LogUtils.e(TAG, "loadImage: invalid request: " + imageRequest);
			if (imageCallback != null) {
				imageCallback.onFailure(imageRequest, new USAException(USAException.PARAMS_ERROR));
			}
			return;
		}
		if (LogUtils.DEBUG) LogUtils.d(TAG, "loadImage: request=" + imageRequest + ", callback=" + imageCallback);
		List<ImageRequest> list = new ArrayList<ImageRequest>();
		list.add(imageRequest);
		loadImages(list, imageCallback);
	}

	@Override
	public void loadImages(List<ImageRequest> imageRequestList, 
			ICocosCallback imageCallback) {
		updateRequest(imageRequestList);
		mTaskDock.downloadBitmap(imageRequestList, imageCallback);
	}
	
	@Override
	public void loadImagePriority(ImageRequest imageRequest, 
			IImageCallback imageCallback) {
	    updateRequest(imageRequest);
		mTaskDock.downloadPriorityTask(imageRequest, imageCallback);
	}
	
	@Override
	public void loadFile(ImageRequest imageRequest, IFileCallback fileCallback) {
	    if (!ImageRequest.checkRequestValid(imageRequest)) {
	        LogUtils.e(TAG, "loadFile: invalid request: " + imageRequest);
	        if (fileCallback != null) {
	            fileCallback.onFailure(imageRequest, new USAException(USAException.PARAMS_ERROR));
	        }
	        return;
	    }
		List<ImageRequest> list = new ArrayList<ImageRequest>();
		list.add(imageRequest);
		loadFiles(list, fileCallback);
	}
	
	@Override
	public void loadFiles(List<ImageRequest> imageRequestList, 
			IFileCallback fileCallback) {
	    updateRequest(imageRequestList);
		mTaskDock.downloadFiles(imageRequestList, fileCallback);
	}
	
	@Override
	public void loadImageFromFile(ImageRequest imageRequest, 
			IImageCallback imageCallback) {
		loadImage(imageRequest, imageCallback);
	}
	
	@Override
	public void loadImageFromWeb(ImageRequest imageRequest, 
			IImageCallback imageCallback) {
	    updateRequest(imageRequest);
		mTaskDock.downloadBitmap4Web(imageRequest, imageCallback);
	}
	
	@Override
	public void recycleBitmap(String url) {
		mCache.recycleBitmap(url);
	}
	
	@Override
	public void stopAllTasks() {
	    if (LogUtils.DEBUG) LogUtils.d(TAG, "stopAllTasks");
		mTaskDock.cancelAllTasks();
	}
	
	@Override
	public void stopTask(String url) {
		mTaskDock.cancelTask(url);
	}
	
	@Override
	public void stopTask(ImageRequest request) {
		if(request != null && request.getUrl() != null){
			mTaskDock.cancelTask(request);
		}
	}

	@Override
	public void openSmoothmode(int cpuCoreCount) {
		mTaskDock.openSmoothmode(cpuCoreCount);
	}

	@Override
	public void closeSmoothmode() {
		mTaskDock.closeSmoothmode();
	}
	
	@Override
	public void setThreadPriority(int priority) {
	    mTaskDock.setThreadPriority(priority);
	}
	
	public void addTask(HttpTask runnableTask) {
		mTaskDock.addTask(runnableTask);
	}
	
	public void addTask(CocosHttpTask runnableTask) {
		mTaskDock.addTask(runnableTask);
	}
	
	public void addSaveTask(Runnable runnableTask) {
	    mTaskDock.addSaveTask(runnableTask);
    }

	@Override
	public void delete(String url) {
		mCache.deleteFile(url);
	}

	@Override
	public String getLocalPath(ImageRequest request) {
		return mCache.getFileRealPath(request);
	}
	
	public void addRunningTask(Runnable runnable) {
		mTaskDock.addRunningTask(runnable);
	}
	
	public void removeRunningTask(Runnable runnable) {
		mTaskDock.removeRunningTask(runnable);
	}
	
	@Override
	public void setEnableScale(boolean enabled) {
	    mEnableScale = enabled;
	}
	
	public boolean getEnableScale() {
	    return mEnableScale;
	}

    public boolean shouldScale(ImageRequest request) {
        return mEnableScale && request.getTargetWidth() > 0 && request.getTargetHeight() > 0;
    }
    
    @Override
    public void setEnableDebugLog(boolean enable) {
        LogUtils.DEBUG = enable;
    }

    @Override
    public void setEnableFastSave(boolean enable) {
        mEnableFastSave = enable;
    }

    @Override
    public boolean isEnableFastSave() {
        return mEnableFastSave;
    }

    @Override
    public void setEnableFullPathCacheKey(boolean enable) {
        mEnableFullPathCacheKey = enable;
    }

    @Override
    public boolean isEnableFullPathCacheKey() {
        return mEnableFullPathCacheKey;
    }
    
    public void setDecodeConfig(Config config) {
        mDecodeConfig = config;
    }
    
    private void updateRequest(List<ImageRequest> imageRequestList) {
        if (imageRequestList != null && !imageRequestList.isEmpty()) {
            for (ImageRequest request : imageRequestList) {
                updateRequest(request);
            }
        }
    }
    private void updateRequest(ImageRequest imageRequest) {
        if (imageRequest == null) {
            return;
        }
        if (shouldScale(imageRequest) && ScaleType.DEFAULT == imageRequest.getScaleType()) {
            imageRequest.setScaleType(ScaleType.CENTER_CROP);
        }
        
        if (mDecodeConfig != null && !imageRequest.isArbitraryDecodeConfig()) {
            imageRequest.setDecodeConfig(mDecodeConfig);
        }
    }
    
    @Override
	public void setCacheMode(CacheMode cacheMode) {
		if (cacheMode == null) {
			return;
		}
		mCache.setCacheMode(cacheMode);
	}
    
    @Override
	public void setFileCacheSize(int cacheSize){
		mCache.setFileCacheSize(cacheSize);
	}

	@Override
	public void setPingbackIsPermit(boolean isPermit) {
		mTaskDock.setIsPingbackPermit(isPermit);
	}
}
