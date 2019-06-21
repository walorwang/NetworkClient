package com.threathunter.networkclient.http.interceptor;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.JsonObject;
import com.threathunter.networkclient.http.constants.NetworkConstants;
import com.threathunter.networkclient.utils.RetrofitClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import androidx.collection.ArrayMap;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求响应基础拦截器
 */
public class BaseInterceptor implements Interceptor {
    private final RetrofitClient retrofitClient;

    public BaseInterceptor(RetrofitClient retrofitClient) {
        this.retrofitClient = retrofitClient;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder originalBuilder;
        ArrayMap<String, String> params = retrofitClient.getAllParams();
        originalBuilder = addReqParams(request, params);
        originalBuilder = addBaseHeader(originalBuilder);

        Response response = chain.proceed(originalBuilder.build());
        saveToken(request, response);
        return response;
    }

    private void saveToken(Request request, Response response) {
        /**
         * @method saveToken
         * @description 保存cookie中的token
         * @date: 2019/5/5 17:51
         * @author: walorwang
         * @param [request, response]
         * @return void
         */
        HttpUrl httpUrl = request.url();
        if (!ObjectUtils.isEmpty(httpUrl)) {
            String url = httpUrl.toString();
            if (!StringUtils.isTrimEmpty(url)) {
                if (url.contains(NetworkConstants.URL_CHECK_REG)) {
                    // 请求token的接口 保存token
                    Headers headers = response.headers();
                    List<Cookie> cookies = Cookie.parseAll(httpUrl, headers);
                    for (Cookie cookie : cookies) {
                        String name = cookie.name();
                        if (NetworkConstants.KEY_TOKEN.equalsIgnoreCase(name)) {
                            SPStaticUtils.put(NetworkConstants.KEY_TOKEN, cookie.value(), true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private Request.Builder addBaseHeader(Request.Builder originalBuilder) {
        /**
         * @method addBaseHeader
         * @description 添加公用请求头
         * @date: 2019/3/28 11:38
         * @author: walorwang
         * @param [originalBuilder]
         * @return void
         */
        String token = SPStaticUtils.getString(NetworkConstants.KEY_TOKEN);
        if (!StringUtils.isTrimEmpty(token)) {
            originalBuilder.addHeader(NetworkConstants.KEY_AUTHOR, token);
        }
        return originalBuilder;
    }

    private Request.Builder addReqParams(Request originalRequest, ArrayMap<String, String> params) {
        /**
         * @method addFormParams
         * @description 利用Builder添加请求参数
         * @date: 2019/3/26 16:59
         * @author: walorwang
         * @param [originalRequest, originalBuilder, params]
         * @return okhttp3.Request.Builder
         */
        Request.Builder originalBuilder;
        if (NetworkConstants.METHOD_GET.equalsIgnoreCase(originalRequest.method())) {
            originalBuilder = addGetParams(originalRequest, params);
        } else {
            originalBuilder = addOtherParams(originalRequest, params);
        }
        return originalBuilder;
    }

    private Request.Builder addOtherParams(Request originalRequest, ArrayMap<String, String> params) {
        /**
         * @method addOtherParams
         * @description 添加其它请求公用参数（已兼容put, delete请求）
         * @date: 2019/3/26 17:06
         * @author: walorwang
         * @param [originalRequest, originalBuilder, params]
         * @return okhttp3.Request.Builder
         */
        RequestBody body = originalRequest.body();
        if (body instanceof FormBody) {
            return addFormParams(originalRequest, params, (FormBody) body);
        } else if (body instanceof MultipartBody) {
            return originalRequest.newBuilder();
        } else {
            // json请求参数等
            return addJsonParams(originalRequest);
        }
    }

    private Request.Builder addFormParams(Request originalRequest, ArrayMap<String, String> params, FormBody body) {
        body = getFormBody(params, body);
        return getFinalBuilder(originalRequest, body);
    }

    private Request.Builder getFinalBuilder(Request originalRequest, RequestBody body) {
        switch (originalRequest.method()) {
            case NetworkConstants.METHOD_DELETE:
                return originalRequest.newBuilder().delete(body);
            default:
                return originalRequest.newBuilder().post(body);
        }
    }

    private FormBody getFormBody(ArrayMap<String, String> params, FormBody body) {
        // 把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (body != null) {
            for (int i = 0; i < body.size(); i++) {
                bodyBuilder.addEncoded(body.encodedName(i), body.encodedValue(i));
            }
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            bodyBuilder.addEncoded(entry.getKey(), entry.getValue());
        }
        body = bodyBuilder.build();
        return body;
    }

    private Request.Builder addJsonParams(Request originalRequest) {
        /**
         * @method addJsonParams
         * @description 添加json请求参数
         * @date: 2019/3/26 17:14
         * @author: walorwang
         * @param [originalRequest]
         * @return okhttp3.Request
         */
        RequestBody body = getJsonBody();
        return getFinalBuilder(originalRequest, body);
    }

    private RequestBody getJsonBody() {
        JsonObject jsonObject = retrofitClient.getCreatJsonObject();
        String json = "";
        if (!ObjectUtils.isEmpty(jsonObject)) {
            json = jsonObject.toString();
        }

        // 是否变换json
        json = retrofitClient.transformJson(json);
        // 返回一个新的RequestBody
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    }

    private Request.Builder addGetParams(Request originalRequest, ArrayMap<String, String> params) {
        /**
         * @method addGetParams
         * @description 添加get请求公用参数
         * @date: 2019/3/26 17:07
         * @author: walorwang
         * @param [originalRequest, params]
         * @return okhttp3.Request.Builder
         */
        HttpUrl.Builder builder = originalRequest.url().newBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return originalRequest.newBuilder().url(builder.build());
    }
}