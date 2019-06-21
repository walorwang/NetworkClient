package com.threathunter.networkclient.http.interceptor;

import com.threathunter.networkclient.http.download.ProgressResponseBody;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        me.goldze.mvvmhabit.http.interceptor
 * @ClassName:      ProgressInterceptor
 * @Description:    加载进度拦截器
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:58
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:58
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class ProgressInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body()))
                .build();
    }
}
