package com.qiyi.imageprovider.base;

import java.util.List;

import com.qiyi.imageprovider.cocos2d.ICocosCallback;
import com.qiyi.imageprovider.logic.CacheMode;

import android.content.Context;
import android.graphics.Bitmap.Config;

public interface IImageProvider {
    /**
     * Initialize ImageProvider with context from outside, reset any internal state if needed.
     * @param context outside context
     */
    void initialize(Context context);
    
    /**
     * Initialize ImageProvider with context from outside, reset any internal state if needed.
     * @param context outside context
     * @param defaultUserId default user id (for url suffix)
     */
    void initialize(Context context, String defaultUserId);
    
	/**
	 * start super load
	 * @param cpuCoreCount
	 */
	void openSmoothmode(int cpuCoreCount);
	/**
	 * Set request dealer thread priority
	 * @param priority
	 */
	void setThreadPriority(int priority);
	
	/**
	 * close super load
	 */
	void closeSmoothmode();
	
	/**
	 * load image
	 * @param imageRequest
	 * @param imageCallback
	 */
	void loadImage(ImageRequest imageRequest, IImageCallback imageCallback);
	
	/**
	 * load image
	 * @param imageRequest
	 * @param imageCallback
	 */
	void loadImage(ImageRequest imageRequest, ICocosCallback imageCallback);
	
	/**
	 * load image from web direct
	 * @param imageRequest
	 * @param imageCallback
	 */
	void loadImageFromWeb(ImageRequest imageRequest, IImageCallback imageCallback);
	
	/**
	 * load images
	 * @param imageRequestList
	 * @param imageCallback
	 */
	void loadImages(List<ImageRequest> imageRequestList, IImageCallback imageCallback);
	
	/**
	 * load images
	 * @param imageRequestList
	 * @param imageCallback
	 */
	void loadImages(List<ImageRequest> imageRequestList, ICocosCallback imageCallback);
	
	/**
	 * priority load image (use caution)
	 * @param imageRequest
	 * @param imageCallback
	 */
	void loadImagePriority(ImageRequest imageRequest, IImageCallback imageCallback);
	
	/**
	 * load file
	 * @param imageRequest
	 * @param fileCallback
	 */
	void loadFile(ImageRequest imageRequest, IFileCallback fileCallback);
	
	/**
	 * load files
	 * @param imageRequestList
	 * @param fileCallback
	 */
	void loadFiles(List<ImageRequest> imageRequestList, IFileCallback fileCallback);
	
	/**
	 * load image from file
	 * @param imageRequest
	 * @param imageCallback
	 */
	void loadImageFromFile(ImageRequest imageRequest, IImageCallback imageCallback);
	
	/**
	 * stop specify task
	 * @param url
	 */
	void stopTask(String url);
	
	/**
	 * stop specify task
	 * @param request
	 */
	void stopTask(ImageRequest request);
	
	/**
	 * shutdown
	 */
	void stopAllTasks();
	
	/**
	 * recycle bitmap 
	 * @param url
	 */
	void recycleBitmap(String url);
	
	/**
	 * delete specify file
	 * @param url
	 */
	void delete(String url);
	
	/**
	 * get Image's local store path
	 * @param request
	 * @return
	 */
	String getLocalPath(ImageRequest request);
	
	void setEnableScale(boolean enabled);

    void setEnableDebugLog(boolean enable);
    
    void setEnableFastSave(boolean enable);
    
    boolean isEnableFastSave();
    
    void setEnableFullPathCacheKey(boolean enable);
    boolean isEnableFullPathCacheKey();
    
    void setDecodeConfig(Config config);
    
    void setCacheMode(CacheMode cacheMode);
    
    void setFileCacheSize(int cacheSize);
    
    void setPingbackIsPermit(boolean isPermit);
}
