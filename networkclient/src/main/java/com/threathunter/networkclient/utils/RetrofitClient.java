package com.threathunter.networkclient.utils;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.JsonUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;
import com.threathunter.basecomponent.Constant;
import com.threathunter.basecomponent.util.DeviceManager;
import com.threathunter.networkclient.BuildConfig;
import com.threathunter.networkclient.base.BaseResponse;
import com.threathunter.networkclient.base.MethodCallback;
import com.threathunter.networkclient.http.ApiDisposableObserver;
import com.threathunter.networkclient.http.StubGsonConverterFactory;
import com.threathunter.networkclient.http.constants.NetworkConstants;
import com.threathunter.networkclient.http.interceptor.BaseInterceptor;
import com.threathunter.networkclient.http.interceptor.ProgressInterceptor;
import com.threathunter.networkclient.http.interceptor.logging.HttpLog;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * RetrofitClient 单例类, 实现网络访问
 */
public class RetrofitClient {

    // 连接读写等超时时间（20s）
    private static final int DEFAULT_TIMEOUT = 20;
    // 缓存大小
    private static final int CACHE_MAX_SIZE = 10 * 1024 * 1024;

    private static Context mContext = Utils.getApp().getApplicationContext();
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;

    private Cache cache = null;
    private File httpCacheDirectory;
    private JsonObject mCreatJsonObject;// 根据map参数创建的json对象
    private JsonObject mAddJsonObject;// 请求参数为单个实体时
    private ArrayMap<String, String> paramMap;// 请求的参数集合
    private boolean mEncodeParams;// 是否加密json形式参数

    public JsonObject getCreatJsonObject() {
        createJsonObject();
        ArrayMap<String, String> allParams = getAllParams();
        Iterator<Map.Entry<String, String>> iterator = allParams.entrySet().iterator();
        if (mAddJsonObject != null) {
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                mAddJsonObject.addProperty(entry.getKey(), entry.getValue());
            }
            return mAddJsonObject;
        } else {
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                mCreatJsonObject.addProperty(entry.getKey(), entry.getValue());
            }
            return mCreatJsonObject;
        }
    }

    public String transformJson(String json) {
        /**
         * @method transformJson
         * @description 变换json：比如对原json数据加密等
         * @date: 2019/4/11 16:22
         * @author: walorwang
         * @param [json]
         * @return java.lang.String
         */
        if (mEncodeParams) {
            // 加密json
            if (!StringUtils.isTrimEmpty(json)) {
                try {
                    String str = AESUtils.encryptString(json, Constant.ENCODE_KEY);
                    JSONObject jsonObject = JsonUtils.getJSONObject("", "", new JSONObject());
                    jsonObject.put("bs", str);// 统一字段bs
                    return jsonObject.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return json;
                }
            }
        }
        return json;
    }

    public void encodeParams(boolean encodeParams) {
        this.mEncodeParams = encodeParams;
    }

    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static String getIconUrl() {
        if (BuildConfig.DEBUG) {
            return NetworkConstants.BASE;
        } else {
            return NetworkConstants.BASE_ONLINE;
        }
    }

    public static String getBaseUrl() {
        if (BuildConfig.DEBUG) {
            return NetworkConstants.BASEURL;
        } else {
            return NetworkConstants.BASEURL_ONLINE;
        }
    }

    @Deprecated
    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public RetrofitClient() {
        this(getBaseUrl());
    }

    public RetrofitClient(String url) {
        this(url, false);
    }

    public RetrofitClient(String url, boolean isProgressInterceptor) {
        /**
         * @method RetrofitClient
         * @description 描述一下方法的作用
         * @date: 2019/3/29 15:55
         * @author: walorwang
         * @param [url, headers, isProgressInterceptor 是否进度拦截]
         * @return
         */
        initRetrofitClient(url, isProgressInterceptor);
    }

    private void initRetrofitClient(String url, boolean isProgressInterceptor) {
        if (TextUtils.isEmpty(url)) {
            url = getBaseUrl();
        }

        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "goldze_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, CACHE_MAX_SIZE);
            }
        } catch (Exception e) {
            Logger.e("Could not create http cache", e);
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext)))
//                .addInterceptor(new CacheInterceptor(mContext))
                .addInterceptor(new BaseInterceptor(this))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS));

        // 处理网络请求的日志拦截输出
        Interceptor logInterceptor;
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
            // 只显示基础log信息
            logInterceptor = new HttpLoggingInterceptor(new HttpLog()).setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logInterceptor = new HttpLoggingInterceptor(new HttpLog()).setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        builder.addInterceptor(logInterceptor);

        if (isProgressInterceptor) {
            builder.addInterceptor(new ProgressInterceptor());
        }
        // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
        okHttpClient = builder.build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(StubGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }

    public <T, E> DisposableObserver create(final Class<T> service,
                                            MethodCallback<T, E> methodCallback,
                                            ApiDisposableObserver<E> apiDisposableObserver) {
        /**
         * @method create
         * @description create 网络请求
         * @date: 2019/3/20 19:02
         * @author: walorwang
         * @param [service]
         * @return T
         */
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }

        T apiService = retrofit.create(service);
        Observable<BaseResponse<E>> observable = methodCallback.bindMethod(apiService, this);
        Observer observer = observable.compose(RxThreadTransform.io2main())
                .compose(RxThreadTransform.exceptionTransformer())
                .subscribeWith(apiDisposableObserver);

        return (DisposableObserver) observer;
    }

    public <T> T create(final Class<T> service) {
        /**
         * @method create
         * @description create apiservice
         * @date: 2019/3/20 19:02
         * @author: walorwang
         * @param [service]
         * @return T
         */
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }

        return retrofit.create(service);
    }

    public RetrofitClient removeParams(List<String> list) {
        /**
         * @method addParams
         * @description 移除多个字段
         * @date: 2019/3/27 10:37
         * @author: walorwang
         * @param [list]
         * @return com.threathunter.networkclient.utils.RetrofitClient
         */
        if (paramMap == null) {
            return this;
        }
        if (!ObjectUtils.isEmpty(list)) {
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (paramMap.containsKey(key)) {
                    paramMap.remove(key);
                }
            }
        }

        return this;
    }

    public RetrofitClient removeParams(String key) {
        /**
         * @method addParams
         * @description 移除单个json字段
         * @date: 2019/3/27 10:33
         * @author: walorwang
         * @param [key, value]
         * @return RetrofitClient
         */
        if (paramMap == null) {
            return this;
        }

        if (paramMap.containsKey(key)) {
            paramMap.remove(key);
        }

        return this;
    }

    public void addParams(Object model) {
        /**
         * @method addParams
         * @description 添加请求的整个实体
         * @date: 2019/3/27 10:37
         * @author: walorwang
         * @param [model]
         * @return com.threathunter.networkclient.utils.RetrofitClient
         */
        if (model != null) {
            // 因为公用参数，只能是jsonobject
            String json = GsonUtils.toJson(model);
            mAddJsonObject = new JsonParser().parse(json).getAsJsonObject();
        } else {
            mAddJsonObject = null;
        }
    }

    public RetrofitClient addParams(ArrayMap<String, String> arrayMap) {
        /**
         * @method addParams
         * @description 添加多个字段
         * @date: 2019/3/27 10:37
         * @author: walorwang
         * @param [paramMap]
         * @return com.threathunter.networkclient.utils.RetrofitClient
         */
        createArrayMap();
        paramMap.putAll((SimpleArrayMap<? extends String, ? extends String>) arrayMap);
        return this;
    }

    public RetrofitClient addParams(String key, String value) {
        /**
         * @method addParams
         * @description 添加单个字段
         * @date: 2019/3/27 10:33
         * @author: walorwang
         * @param [key, value]
         * @return org.json.JSONObject
         */
        createArrayMap();
        paramMap.put(key, value);
        return this;
    }

    private void createJsonObject() {
        if (mCreatJsonObject == null) {
            mCreatJsonObject = new JsonObject();
        }
    }

    private void getBaseParams() {
        createArrayMap();
        if (StringUtils.isTrimEmpty(paramMap.get(NetworkConstants.KEY_UUID))) {
            DeviceManager.getInstance().getUUID(new DeviceManager.GenUUIDCallback() {
                @Override
                public void onGenUUIDSuccess(String uuid) {
                    paramMap.put(NetworkConstants.KEY_UUID, uuid);
                }
            });
        }
    }

    private void createArrayMap() {
        if (paramMap == null) {
            paramMap = new ArrayMap<>();
        }
    }

    public ArrayMap<String, String> getAllParams() {
        /**
         * @method getAllParams
         * @description 获取所有的key-value 请求参数
         * @date: 2019/6/19 15:12
         * @author: walorwang
         * @param []
         * @return androidx.collection.ArrayMap<java.lang.String, java.lang.String>
         */
        getBaseParams();
        return paramMap;
    }
}
