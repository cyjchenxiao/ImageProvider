package com.qiyi.imageprovider.util;

import java.util.Locale;

import com.qiyi.video.utils.MD5Utils;

import android.content.Context;

/**
 * Maintains outside context related stuff (mac address/user id/...).
 */
public class ContextUtils {
    private static final String TAG = "ImageProvider/ContextUtils";
    private static final String DEFAULT_MAC_ADDR = "00:00:00:00:00:00";
    
    private static ContextUtils sInstance;
    
    private String mEtherMac;
    private String mWifiMac;
    private String mDefaultUserId;
    
    private ContextUtils() { }
    
    public static synchronized ContextUtils instance() {
        if (sInstance == null) {
            sInstance = new ContextUtils();
        }
        return sInstance;
    }
    
    public void initialize(Context context) {
        mEtherMac = SysUtils.getEtherMac();
        mWifiMac = SysUtils.getWifiMac(context);
        mDefaultUserId = getMd5LoweredFormatMacAddr(getMacAddr()).toLowerCase(Locale.ENGLISH);
        LogUtils.d(TAG, "initialize: ether mac=" + mEtherMac + ", wifi mac=" + mWifiMac
                + ", default user id=" + mDefaultUserId);
    }
    
    public void initialize(Context context, String defaultUserId) {
        mEtherMac = SysUtils.getEtherMac();
        mWifiMac = SysUtils.getWifiMac(context);
        mDefaultUserId = !StringUtils.isEmpty(defaultUserId) ?
                defaultUserId : getMd5LoweredFormatMacAddr(getMacAddr()).toLowerCase(Locale.ENGLISH);
        LogUtils.d(TAG, "initialize: ether mac=" + mEtherMac + ", wifi mac=" + mWifiMac
                + ", default user id=" + mDefaultUserId);
    }
    
    public String getEtherMac() {
        return mEtherMac;
    }
    
    public String getWifiMac() {
        return mWifiMac;
    }
    
    public String getDefaultUserIdFromMacAddr() {
        return mDefaultUserId;
    }
    
    public String getMacAddr() {
        return !StringUtils.isEmpty(mEtherMac) ? mEtherMac :
            (!StringUtils.isEmpty(mWifiMac) ? mWifiMac : DEFAULT_MAC_ADDR);
    }
    
    private static String getFormattedMacAddr(String macAddr) {
        return macAddr.replaceAll("-", "").replaceAll(":", "").replaceAll("\\.", "");
    }
    
    private static String getMd5UpperedFormatMacAddr(String macAddr) {
        return MD5Utils.MD5(getFormattedMacAddr(macAddr).toUpperCase(Locale.ENGLISH));
    }
    
    private static String getMd5LoweredFormatMacAddr(String macAddr) {
        return MD5Utils.MD5(getFormattedMacAddr(macAddr).toLowerCase(Locale.ENGLISH));
    }
}
