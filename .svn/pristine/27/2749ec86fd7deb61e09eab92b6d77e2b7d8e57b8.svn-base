package com.qiyi.imageprovider.logic.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.logic.CacheMode;
import com.qiyi.imageprovider.util.BitmapTool;
import com.qiyi.imageprovider.util.FileTool;
import com.qiyi.imageprovider.util.LogUtils;
import com.qiyi.imageprovider.util.StringUtils;

class CacheFile {
    private static final String TAG = "ImageProvider/CacheFile";
//    @SuppressLint("SdCardPath")
    
    private static final String CACHE_PATH = "/data/data/";
    private static final String NORMAL_PATH = "/cache/qiyiimages/";
    private static final String LASTING_PATH = "/files/";
    private static final String BASE_CACHE_PATH = "/cache/qiyiimages/";
    private static final String DEFAULT_PKG_NAME = "com.qiyi.video";
    private static final int FILES_QUEUE_SIZE_DEFAULT = 800;
    private static final int FILES_QUEUE_SIZE_LASTING_DEFAULT = 100;
    
    private String mPathNormal = null;
    private String mPathLasting = null;
    private String mPathCache = null;

    private Map<String, String> mFilesMap = new HashMap<String, String>();
    private int mFileQueueSize;
    private CacheMode mCacheMode = CacheMode.DATA_FIRST;

	protected CacheFile() {
        mPathNormal = CACHE_PATH + DEFAULT_PKG_NAME + NORMAL_PATH;
        mPathLasting = CACHE_PATH + DEFAULT_PKG_NAME + LASTING_PATH;
        mPathCache = BASE_CACHE_PATH;
        LogUtils.d(TAG, ">>>>> CacheFile() --- mFileQueueSize: " + mFileQueueSize);
        initCache(mPathNormal, mFileQueueSize <=0 ? FILES_QUEUE_SIZE_DEFAULT : mFileQueueSize);
        initCache(mPathLasting, FILES_QUEUE_SIZE_LASTING_DEFAULT);
        if (mCacheMode == CacheMode.CACHE_FIRST) {
        	initCache(mPathCache, mFileQueueSize <=0 ? FILES_QUEUE_SIZE_DEFAULT : mFileQueueSize);
        }
    }

    public void initData(Context context) {
        if (context != null) {
            String packageName = context.getPackageName();
            LogUtils.d(TAG, "setContext : " + packageName);
            mPathNormal = CACHE_PATH + packageName + NORMAL_PATH;
            mPathLasting = CACHE_PATH + packageName + LASTING_PATH;
            mPathCache = BASE_CACHE_PATH;
            LogUtils.d(TAG, ">>>>> initData() --- mFileQueueSize: " + mFileQueueSize);
            initCache(mPathNormal, mFileQueueSize <=0 ? FILES_QUEUE_SIZE_DEFAULT : mFileQueueSize);
            initCache(mPathLasting, FILES_QUEUE_SIZE_LASTING_DEFAULT);
            if (mCacheMode == CacheMode.CACHE_FIRST) {
            	initCache(mPathCache, mFileQueueSize <=0 ? FILES_QUEUE_SIZE_DEFAULT : mFileQueueSize);
            }
        }
    }

    private void initCache(String path, int size) {
    	LogUtils.d(TAG, ">>>>> initCache() --- path: " + path);
        File folder = new File(path);
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            return;
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        if (!neatFilesAsync(path, files, size)) {
            addFiles(files);
        }
    }

    /**
     * clear excess files
     * 
     * @param path
     * @param files
     * @param size
     * @return
     */
    private boolean neatFilesAsync(final String path, File[] files, final int size) {
        if (files.length < size) {
            return false;
        }
        deleteFiles(new IDeleteCallback() {
            @Override
            public void onCompleted() {
            	LogUtils.d(TAG, ">>>> neatFilesAsync() ---- deleteFiles --- onCompleted()"); 
                initCache(path, size);
            }
        }, files, size);
        return true;
    }

    /**
     * add files
     * 
     * @param files
     */
    private void addFiles(File[] files) {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile() && !mFilesMap.containsKey(file.getName())) {
                mFilesMap.put(file.getName(), file.getAbsolutePath());
            }
        }
    }

    /**
     * get bitmap
     * 
     * @param url
     * @return
     */
    protected Bitmap getBitmap(ImageRequest request) {
        Bitmap bitmap = null;
        String filePath = getFilePath(request);
        if (LogUtils.DEBUG) LogUtils.d(TAG, "getBitmap: filePath=" + filePath);
        if (filePath != null) {
            bitmap = BitmapTool.createBitmap(filePath, request.getDecodeConfig());
        }
        return bitmap;
    }

//    @SuppressLint("NewApi")
    protected String getFilePath(ImageRequest request) {
        String filename = FileTool.getFileNameFromRequest(request);
        if (mFilesMap.containsKey(filename)) {
            return mFilesMap.get(filename);
        } else {
            return null;
        }
    }

    protected String recordFile(ImageRequest imageRequest, byte[] bufferBytes) {
        String saveUrl;
        String savePath = imageRequest.getSavePath();
        String filename = FileTool.getFileNameFromRequest(imageRequest);
        if (savePath != null && !savePath.equals("")) {
        	LogUtils.d(TAG, ">>>>> recordFile() --- writeFile to savePath");
        	saveUrl = writeFile(savePath, filename, bufferBytes);
        } else if (imageRequest.isLasting()) {
            saveUrl = writeFile(mPathLasting, filename, bufferBytes);
        } else {
        	if (mCacheMode == CacheMode.CACHE_FIRST) {
				LogUtils.d(TAG, ">>>>> recordFile() --- writeFile to cache");
				saveUrl = writeFile(mPathCache, filename, bufferBytes);
				if (StringUtils.isEmpty(saveUrl)) {
					LogUtils.d(TAG, ">>>>> recordFile() --- writeFile to data/data");
					saveUrl = writeFile(mPathNormal, filename, bufferBytes);
				}
			} else {
				LogUtils.d(TAG, ">>>>> recordFile() --- writeFile to data/data");
        		saveUrl = writeFile(mPathNormal, filename, bufferBytes);
			}
        }
        return saveUrl;
    }

    private String writeFile(String savePath, String filename, byte[] bufferBytes) {
        if (savePath == null) {
            return null;
        }

        FileTool.createFolder(savePath);
        File file = new File(savePath + filename + ".tmp");
        if (LogUtils.DEBUG) LogUtils.d(TAG, "writeFile: path=" + file.getAbsolutePath());
        try {
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bufferBytes);
                fileOutputStream.close();
                file.renameTo(new File(savePath + filename));
                mFilesMap.put(filename, savePath + filename);
            }
            return savePath + filename;
        } catch (IOException ex) {
            LogUtils.e(TAG, "writeFile: exception happened", ex);
            if (file.exists()) {
                file.delete();
            }
        }
        return null;
    }
//    
//    private String writeFileToSDCard(String filename, byte[] bufferBytes){
//        
//    	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//若sd卡存在
//    		File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
//    		FileTool.createFolder(sdCardDir.getPath() + SDCARD_PATH);
//    		File file = new File(sdCardDir, SDCARD_PATH + filename + ".tmp");
//    		if (LogUtils.DEBUG) LogUtils.d(TAG, "writeFile: path=" + file.getAbsolutePath());
//            try {
//            	/* SD卡还有无剩余空间用来保存图片 */
//        		long freeSize = FileTool.getSDFreeSize();
//        		if (freeSize < SDCARD_MIN_AVAILABLE_SIZE) {
//        			LogUtils.e(TAG, ">>>>> writeFileToSDCard failure!!! ---- no free size");
//    				return null;
//    			}
//            	
//        		/* 向SD卡中写入文件 */
//                if (file.exists()) {
//                    file.delete();
//                }
//                if (file.createNewFile()) {
//                    FileOutputStream fileOutputStream = new FileOutputStream(file);
//                    fileOutputStream.write(bufferBytes);
//                    fileOutputStream.close();
//                    file.renameTo(new File(sdCardDir.getPath() + SDCARD_PATH + filename));
//                    mFilesMap.put(filename, sdCardDir.getPath() + SDCARD_PATH + filename);
//                }
//                String path = sdCardDir.getPath() + SDCARD_PATH + filename;
//                LogUtils.d(TAG, ">>>>> writeFileToSDCard success! ---- " + path);
//                return path;
//            } catch (Exception ex) {//SD卡存储失败
//                LogUtils.e(TAG, ">>>>> writeFileToSDCard: exception happened", ex);
//                if (file.exists()) {
//                    file.delete();
//                }
//                return null;
//            }
//    	}
//    	return null;
//    }

    protected void deleteSpecifyFile(String url) {
        File file = new File(url);
        if (file != null && file.isFile()) {
            try {
                file.delete();
            } catch (Exception ex) {
            }
        }
    }

    private static final int REMOVE_TAIO = 2;

    private static void deleteFiles(final IDeleteCallback callback, final File[] files, final int limittotal) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int count = files.length / REMOVE_TAIO;
                    if (count > limittotal) {
                        count = files.length - limittotal - limittotal / 2;
                    }
                    for (int i = 0; i < count; i++) {
                        File file = files[i];
                        if (file.exists() && file.isFile()) {
                            file.delete();
                        }
                    }
                    callback.onCompleted();
                } catch (Exception ex) {
                    callback.onCompleted();
                }
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public interface IDeleteCallback {
        public void onCompleted();
    }
    
	public void setFileQueueSize(int fileQueueSize) {
		this.mFileQueueSize = fileQueueSize;
	}

	public void setCacheMode(CacheMode cacheMode) {
		LogUtils.d(TAG, ">>>>>setCacheMode --- " + cacheMode);
		this.mCacheMode = cacheMode;
	}
}
