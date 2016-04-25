package com.qiyi.imageprovider.logic.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.util.UrlTool;

class CacheMemory {
	private Map<String, SoftReference<Bitmap>> mBitmapMap; 
	
	CacheMemory(){
		mBitmapMap = new HashMap<String, SoftReference<Bitmap>>();
	}
	
	/**
	 * save bitmap to Memory
	 * @param url
	 * @param bitmap
	 */
	protected void save(ImageRequest imageRequest, Bitmap bitmap) {
		save(UrlTool.getModifiedUrlFromRequest(imageRequest), bitmap);
	}
	
	protected void save(String url, Bitmap bitmap) {
		synchronized(mBitmapMap) {
			mBitmapMap.put(url, new SoftReference<Bitmap>(bitmap));
		}
	}

	/**
	 * get bitmap
	 * @param url
	 * @return
	 */
	protected Bitmap get(String url) {
		if (!mBitmapMap.containsKey(url)) {
			return null;
		}
		SoftReference<Bitmap> weakBitmap = mBitmapMap.get(url);
		if (weakBitmap != null) {
			Bitmap bitmap = weakBitmap.get();
			if (bitmap != null) {
				if (!bitmap.isRecycled()) {
					return bitmap;
				}
			}
		}
		synchronized(mBitmapMap) {
			mBitmapMap.remove(url);
		}
		return null;
	}
	
	/**
	 * recycle bitmap
	 * @param url
	 */
	protected synchronized void recycle(String url) {
		synchronized(mBitmapMap) {
			SoftReference<Bitmap> weakBitmap = mBitmapMap.get(url);
			if (weakBitmap != null) {
				Bitmap bitmap = weakBitmap.get();
				if (bitmap != null) {
					mBitmapMap.remove(url);
				}
			}
		}
	}
}
