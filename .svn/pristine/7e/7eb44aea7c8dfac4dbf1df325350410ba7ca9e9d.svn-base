package com.qiyi.imageprovider.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.base.ImageRequest.ScaleType;
import com.qiyi.imageprovider.logic.ImageProvider;
import com.qiyi.video.utils.MD5Utils;

public class FileTool {
    
    private FileTool() { }
    
    private static final String TAG = "ImageProvider/FileTool";
    private final static String SPACE = "_";
	public static String getFileNameFromRequest(ImageRequest request) {
	    if (request == null || StringUtils.isEmpty(request.getUrl())) {
	        return null;
	    }
	    String url = request.getUrl();
	    String result = null;
        String suffixes = getFileSuffixesFromRequest(request);
        String fileName = null;
        if (ImageProvider.get().isEnableFullPathCacheKey()) {
            String formattedUrl = url.replaceAll("://", "_").replaceAll("/", "_");
            if (LogUtils.DEBUG) LogUtils.d(TAG, "getFileNameFromRequest: formatted url=" + formattedUrl);
            fileName = MD5Utils.MD5(formattedUrl);
        } else {
            fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
	    }
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            result = fileName + suffixes;
        } else {
            String extension = fileName.substring(index);
            result = fileName.substring(0, index) + suffixes + extension;
        }
        if (LogUtils.DEBUG) LogUtils.d(TAG, "getFileNameFromRequest() returns " + result);
        return result;
	}
	
	private static String getFileSuffixesFromRequest(ImageRequest request) {
	    StringBuilder builder = new StringBuilder();
        if (ImageProvider.get().shouldScale(request)) {
            builder.append(SPACE).append(request.getTargetWidth())
                .append(SPACE).append(request.getTargetHeight());
        }
        if (request.getScaleType() == ScaleType.CENTER_INSIDE) {
            builder.append(SPACE).append("center_inside").append("[").append(request.getTargetWidth()).append("x").append(request.getTargetHeight()).append("]");
        }
        
        
    /*       
     * TV6.0 is no need to clip corners for bitmap
     * 
     *  if (request.shouldRoundCorner()) {
            builder.append(SPACE);
            // corner specifications: tl/tr/bl/br - top left/top right/bottom left/bottom right; size follows each label
            CornerSpec[] specs = request.getRoundCornerSpecs();
            CornerSpecUtils.sortAsc(specs);
            for (CornerSpecUtils.CornerSpec spec : specs) {
                builder.append(spec.getCorner().getDescription()).append((int) spec.getRadius());
            }
        }*/
        
        return builder.toString();
	}
	
	public static void createFolder(String path) {
		File rootFolder = new File(path);
		if (!rootFolder.exists()) {
			rootFolder.mkdirs();
		}
	}
	
	public static long getSDFreeSize(){  
	     //取得SD卡文件路径  
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		return freeBlocks * blockSize; // 单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		// return (freeBlocks * blockSize)/1024 /1024; //单位MB
   }    
	
    public static long getSDTotalSize(){
	     //取得SD卡文件路径  
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		
		long blockSize = sf.getBlockSize();
    	long totalblocks=sf.getBlockCount();
    	return totalblocks * blockSize ;
    }
}
