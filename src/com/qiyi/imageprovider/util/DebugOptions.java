package com.qiyi.imageprovider.util;

import java.util.concurrent.ConcurrentHashMap;

import com.qiyi.video.utils.SysPropUtils;

public class DebugOptions {
    private static final String DBGOPT_POOL = "qiyi.imageprovider.pool";
    private static final String DBGOPT_IMAGE_DECODE_PRIORITY = "qiyi.imageprovider.priority";
    private static final String DBGOPT_LOG = "qiyi.imageprovider.debug.log";
    private static final String DBGOPT_ENABLE_URL_SUFFIX = "qiyi.imageprovider.url.suffix";
    private static final String DBGOPT_ENABLE_MEM_CACHE = "qiyi.imageprovider.memcache";
    private static final String DBGOPT_ENABLE_FILE_CACHE = "qiyi.imageprovider.filecache";
    
    private static ConcurrentHashMap<String, Object> sKeyValCache = new ConcurrentHashMap<String, Object>();
    
    private DebugOptions() { }

    public static boolean isEnableMemoryCache() {
        return fetchAndRecordBoolean(DBGOPT_ENABLE_MEM_CACHE, true);
    }
    
    public static boolean isEnableFileCache() {
        return fetchAndRecordBoolean(DBGOPT_ENABLE_FILE_CACHE, true);
    }
    
    public static boolean isPoolEnabled() {
        return fetchAndRecordBoolean(DBGOPT_POOL, false);
    }

    public static boolean isLowDecodePriority() {
        return fetchAndRecordBoolean(DBGOPT_IMAGE_DECODE_PRIORITY, true);
    }

    public static boolean isOpenDebugLog() {
        return fetchAndRecordBoolean(DBGOPT_LOG, false);
    }

    public static boolean isAddSuffixForUrl() {
        return fetchAndRecordBoolean(DBGOPT_ENABLE_URL_SUFFIX, false);
    }
    
    private static boolean fetchAndRecordBoolean(String key, boolean def) {
        if (sKeyValCache.containsKey(key)) {
            return (Boolean) sKeyValCache.get(key);
        }
        boolean ret = SysPropUtils.getBoolean(key, def);
        sKeyValCache.put(key, ret);
        return ret;
    }
    
    public static void reset() {
        sKeyValCache.clear();
    }

}
