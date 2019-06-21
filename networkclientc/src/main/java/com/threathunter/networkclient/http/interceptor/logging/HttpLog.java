package com.threathunter.networkclient.http.interceptor.logging;

import android.util.Log;

import com.threathunter.networkclient.http.constants.NetworkConstants;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 *
 * @ProjectName: PrivacySecurity
 * @Package: com.threathunter.networkclient.http.interceptor.logging
 * @ClassName: HttpLog
 * @Description: 接口请求响应的日志
 * @Author: walorwang
 * @CreateDate: 2019/4/2 17:21
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/4/2 17:21
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

public class HttpLog implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Log.d(NetworkConstants.TAG, message);
    }
}
