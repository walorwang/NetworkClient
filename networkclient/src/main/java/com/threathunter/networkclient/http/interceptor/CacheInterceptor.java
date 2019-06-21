package com.threathunter.networkclient.http.interceptor;

import android.content.Context;
import com.blankj.utilcode.util.NetworkUtils;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.http.interceptor
 * @ClassName:      CacheInterceptor
 * @Description:    无网络状态下智能读取缓存的拦截器
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:59
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:59
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class CacheInterceptor implements Interceptor {

    private Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (NetworkUtils.isConnected()) {
            Response response = chain.proceed(request);
            // read from cache for 60 s
            int maxAge = 60;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            //读取缓存信息
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            //set cache times is 3 days
            int maxStale = 60 * 60 * 24 * 3;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    }
}
