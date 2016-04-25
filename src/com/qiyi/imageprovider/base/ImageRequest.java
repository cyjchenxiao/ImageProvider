package com.qiyi.imageprovider.base;

import java.util.Arrays;

import android.graphics.Bitmap;

import com.qiyi.imageprovider.model.RequestQueue;
import com.qiyi.imageprovider.util.CornerSpecUtils;
import com.qiyi.imageprovider.util.StringUtils;

public class ImageRequest {
	private String mUrl;
	private Object mCookie;
	private boolean mIsLasting;
	private String mSavePath;
	private ImageType mType = ImageType.DEFAULT;
	private float mRadius = 32.0f;
	private int mTargetWidth = 0;
	private int mTargetHeight = 0;
	private Bitmap.Config mDecodeConfig = Bitmap.Config.ARGB_8888;
	private boolean mArbitraryDecodeConfig;
	
	private CornerSpecUtils.CornerSpec[] mCornerSpecs;
    private ScaleType mScaleType = ScaleType.DEFAULT;
	
	public enum ImageType {
	    DEFAULT, RECT, ROUND
	}
	
	public enum ScaleType {
	    DEFAULT,
	    CENTER_INSIDE,
	    CENTER_CROP,
	}
	
	public ImageRequest(String imageUrl) {
		this.mUrl = imageUrl;
	}
	
	public ImageRequest(String imageUrl, Object cookie) {
		this.mUrl = imageUrl;
		this.mCookie = cookie;
	}
	
	public void setLasting(boolean isLasting) {
		this.mIsLasting = isLasting;
	}

	public String getUrl() {
		return mUrl;
	}

	public Object getCookie() {
		return mCookie;
	}

	public boolean isLasting() {
		return mIsLasting;
	}
	
	public void setSavePath(String savePath) {
		mSavePath = savePath;
	}
	
	public String getSavePath() {
		return mSavePath;
	}
	
	public void setImageType(ImageType type) {
	    mType = type;
	}
	
	public ImageType getImageType() {
	    return mType;
	}
	
	public void setScaleType(ScaleType scaleType) {
	    mScaleType = scaleType;
	}
	public ScaleType getScaleType() {
	    return mScaleType;
	}
	
	public void setRadius(float radius) {
	    mRadius = radius;
	}
	
	public float getRadius() {
	    return mRadius;
	}
	
	public int getTargetWidth() {
	    return mTargetWidth;
	}
	
	public void setTargetWidth(int width) {
	    mTargetWidth = width;
	}
	
	public int getTargetHeight() {
	    return mTargetHeight;
	}
	
	public void setTargetHeight(int height) {
	    mTargetHeight = height;
	}
	

	
	public void setDecodeConfig(Bitmap.Config config) {
	    mDecodeConfig = config;
	    mArbitraryDecodeConfig = true;
	}
	
	public Bitmap.Config getDecodeConfig() {
	    return mDecodeConfig;
	}
	
	/*package*/ public boolean isArbitraryDecodeConfig() {
	    return mArbitraryDecodeConfig;
	}
	
	private RequestQueue mSameTaskQueue = new RequestQueue();
	private boolean mShouldBeKilled = true;
	
	public RequestQueue getSameTaskQueue() {
		return mSameTaskQueue;
	}
	
	public boolean getShouldBeKilled() {
	    return mShouldBeKilled;
	}
	
	public void setShouldBeKilled(boolean kill) {
	    mShouldBeKilled  = kill;
	}

	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof ImageRequest) {
			ImageRequest r = (ImageRequest) o;
			String url = r.getUrl();
			url = (url == null ? "" : url);
			boolean ret = url.equals(mUrl);
			Object cookie = getCookie();
			cookie = (cookie == null ? new Object() : cookie);
			ret &= cookie == r.getCookie();
			ret &= mDecodeConfig == r.mDecodeConfig;
			ret &= mTargetWidth == r.mTargetWidth;
			ret &= mTargetHeight == r.mTargetHeight;
			ret &= mSavePath == r.mSavePath || mSavePath != null && mSavePath.equals(r.mSavePath);
			ret &= mRadius == r.mRadius;
			ret &= mType == r.mType;
			return ret;
		} 
		return false;
	}
	
	@Override
	public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("ImageRequest@");
	    builder.append(Integer.toHexString(this.hashCode()));
	    builder.append("{");
	    builder.append("url=");
	    builder.append(mUrl);
//	    builder.append(", cookie=");
//	    builder.append(getObjectDescription(mCookie));
	    builder.append(", isLasting=");
	    builder.append(mIsLasting);
	    builder.append(", target w/h=");
        builder.append(mTargetWidth).append("/").append(mTargetHeight);
        builder.append(", radius=");
        builder.append(mRadius);
	    builder.append(", savePath=");
	    builder.append(mSavePath);
	    builder.append(", scaleType=").append(mScaleType);
	    builder.append(", cornerSpecs=").append(Arrays.toString(mCornerSpecs));
	    builder.append(", decodeConfig=").append(mDecodeConfig);
	    builder.append("}");
	    return builder.toString();
	}
	
	public static boolean checkRequestValid(ImageRequest request) {
        if (request == null) {
            return false;
        }
        
        String url = request.getUrl();
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        
        return true;
    }

    @SuppressWarnings("unused")
	private static String getObjectDescription(Object obj) {
	    if (obj == null) {
	        return "NULL";
	    }
	    
	    return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
	}
    
/*	
 * TV6.0 is no need to clip corners for bitmap
 * 
 * public void setRoundCornerSpecs(CornerSpec... specs) {
	    mCornerSpecs = specs;
	}
	
	public void setRoundCornerSpecs(List<CornerSpec> specs) {
	    if (specs != null && specs.size() > 0) {
	        mCornerSpecs = specs.toArray(new CornerSpec[specs.size()]);
	    }
	}
	
	public CornerSpecUtils.CornerSpec[] getRoundCornerSpecs() {
	    return mCornerSpecs;
	}
	
	public boolean shouldRoundCorner() {
	    return mCornerSpecs != null && mCornerSpecs.length > 0;
	}*/
}
