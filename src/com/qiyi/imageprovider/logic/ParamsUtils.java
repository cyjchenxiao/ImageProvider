package com.qiyi.imageprovider.logic;

import java.util.List;

import com.qiyi.imageprovider.base.IFileCallback;
import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.model.USAException;

public class ParamsUtils {
	/**
	 * check params
	 * @param imageRequestList
	 * @param imageCallback
	 * @return
	 */
	public static boolean isCorrectParams(List<ImageRequest> imageRequestList, 
			IImageCallback imageCallback) {
		if(imageCallback == null){
			return false;
		}
		
		if(imageRequestList == null || imageRequestList.size() == 0){
			imageCallback.onFailure(null, 
					new USAException(USAException.PARAMS_ERROR));
			return false;
		}
		
		return true;
	}
	
	/**
	 * check params
	 * @param imageRequest
	 * @param imageCallback
	 * @return
	 */
	public static boolean isCorrectParams(ImageRequest imageRequest, 
			IImageCallback imageCallback) {
		if (imageCallback == null) {
			return false;
		}
		
		if(imageRequest == null){
			imageCallback.onFailure(imageRequest, 
					new USAException(USAException.PARAMS_ERROR));
			return false;
		}
		
		return true;
	}
	
	/**
	 * check params
	 * @param imageRequestList
	 * @param fileCallback
	 * @return
	 */
	public static boolean isCorrectParams(List<ImageRequest> imageRequestList, 
			IFileCallback fileCallback) {
		if(fileCallback == null){
			return false;
		}
		
		if(imageRequestList == null || imageRequestList.size() == 0){
			fileCallback.onFailure(null, 
					new USAException(USAException.PARAMS_ERROR));
			return false;
		}
		
		return true;
	}
}
