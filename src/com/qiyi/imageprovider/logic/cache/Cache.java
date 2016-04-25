package com.qiyi.imageprovider.logic.cache;

import android.content.Context;
import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.logic.CacheMode;

public class Cache {
	private static Cache gCache = new Cache();
	
	private CacheFile mCacheFile;
	private CacheMemory mMemoryCache;
	
	private boolean mIsWriteFile = true;
	
	private Cache() {
		mCacheFile = new CacheFile();
		mMemoryCache = new CacheMemory();
	}
	
	public static Cache get() {
		return gCache;
	}
	
	public void setContext(Context context) {
		mCacheFile.initData(context);
	}
	
	public void setFileCacheSize(int fileCacheSize){
		mCacheFile.setFileQueueSize(fileCacheSize);
	}
	
	public void setWriteFile(boolean isWrite) {
		mIsWriteFile = isWrite;
	}
	
	public Bitmap getMemoryBitmap(String url) {
		return mMemoryCache.get(url);
	}
	
	public String getFileBitmapPath(ImageRequest request) {
		return mCacheFile.getFilePath(request);
	}
	
	public Bitmap getFileBitmap(ImageRequest request, boolean isRecordMemory) {
		Bitmap bitmap = mCacheFile.getBitmap(request);
		if (bitmap != null && isRecordMemory) {
			mMemoryCache.save(request, bitmap);
		}
		return bitmap;
	}
	
	public String getFileRealPath(ImageRequest request) {
		return mCacheFile.getFilePath(request);
	}
	
	public void recordMemory(ImageRequest imageRequest, Bitmap bitmap) {
		mMemoryCache.save(imageRequest, bitmap);
	}
	
	public String recordFile(ImageRequest imageRequest, byte[] bytesBuffer) {
		if(!mIsWriteFile) {
			return null;
		}
		return mCacheFile.recordFile(imageRequest, bytesBuffer);
	}
	
	public String recordFileMust(ImageRequest imageRequest, byte[] bytesBuffer) {
		return mCacheFile.recordFile(imageRequest, bytesBuffer);
	}
	
	public void recycleBitmap(String url) {
		mMemoryCache.recycle(url);
	}
	
	public void deleteFile(String url) {
		mCacheFile.deleteSpecifyFile(url);
	}

	public void setCacheMode(CacheMode cacheMode) {
		mCacheFile.setCacheMode(cacheMode);
	}
	
}
