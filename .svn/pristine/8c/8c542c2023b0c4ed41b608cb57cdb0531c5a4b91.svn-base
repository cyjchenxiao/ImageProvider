package com.qiyi.imageprovider.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.qiyi.video.utils.LogUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public final class SysUtils {
    private static final String TAG = "ImageProvider/SysUtils";

    /** 获取文件MAC地址 /sys/class/net/eth0/address */
    public static String getEtherMac() {
        Reader reader = null;
        StringBuffer sbuffer = new StringBuffer();
        try {
            char[] buffer = new char[20];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream("/sys/class/net/eth0/address"));
            while ((charread = reader.read(buffer)) != -1) {
                if ((charread == buffer.length) && (buffer[buffer.length - 1] != '\r')) {
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (buffer[i] != '\r') {
                            sbuffer.append(buffer[i]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "getEtherMac: exception happened:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    LogUtils.e(TAG, "getEtherMac: exception happened when close:", e);
                }
            }
        }

        return sbuffer.toString().trim();
    }

    /** 获取无线网卡mac地址 */
    public static String getWifiMac(Context ctx) {
        try {
            WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (wifiMgr == null) ? null : wifiMgr.getConnectionInfo();
            if (info != null) {
                return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
