package com.qiyi.imageprovider.util;

import com.qiyi.imageprovider.base.ImageRequest;

public class UrlTool {
    private static final String TAG = "ImageProvider/UrlTool";
    private static final String URL_SUFFIX_HEADER = "qyid=tv_";//"?" or "&" depends on original url
    private static final String SYS_TIME_SUFFIX = "_" + String.valueOf(System.currentTimeMillis());
    private static String sRequestSuffix;
    
    private UrlTool() { }
    
    public static String getModifiedUrlFromRequest(ImageRequest request) {
        String url = request.getUrl();
        String fileName = FileTool.getFileNameFromRequest(request);
        int index = url.lastIndexOf('/');
        if (index > 0) {
            url = url.substring(0, index + 1) + fileName;
        }
        return url;
    }
    
    public static String getRequestSuffix() {
        if (!StringUtils.isEmpty(sRequestSuffix)) {
            return sRequestSuffix;
        }
        sRequestSuffix = URL_SUFFIX_HEADER + ContextUtils.instance().getDefaultUserIdFromMacAddr() + SYS_TIME_SUFFIX;
        return sRequestSuffix;
    }
    
}
