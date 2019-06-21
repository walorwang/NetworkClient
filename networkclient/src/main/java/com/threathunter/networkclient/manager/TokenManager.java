package com.threathunter.networkclient.manager;

import com.blankj.utilcode.util.SPStaticUtils;
import com.threathunter.basecomponent.Constant;
import com.threathunter.networkclient.base.MethodCallback;
import com.threathunter.networkclient.base.ResultCallback;
import com.threathunter.networkclient.http.ApiDisposableObserver;
import com.threathunter.networkclient.base.BaseResponse;
import com.threathunter.networkclient.services.NetworkApiService;
import com.threathunter.networkclient.utils.RetrofitClient;
import com.threathunter.networkclient.utils.RetrofitFactory;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 *
 * @ProjectName: PrivacySecurity
 * @Package: com.threathunter.networkclient.manager
 * @ClassName: TokenManager
 * @Description: java类作用描述
 * @Author: walorwang
 * @CreateDate: 2019/3/29 12:46
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/3/29 12:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class TokenManager {

    private static class Holder {
        private static final TokenManager INSTANCE = new TokenManager();
    }

    public static TokenManager getInstance() {
        return Holder.INSTANCE;
    }

    public void clearToken() {
        SPStaticUtils.remove(Constant.KEY_TOKEN, true);
    }

    public String getToken() {
        /**
         * @method getToken
         * @description 获取token
         * @date: 2019/3/29 12:40
         * @author: walorwang
         * @param []
         * @return java.lang.String
         */
        return SPStaticUtils.getString(Constant.KEY_TOKEN);
    }

    public DisposableObserver loadToken(ResultCallback callback) {
        DisposableObserver disposableObserver = RetrofitFactory.getInstance()
                .creatClient()
                .create(NetworkApiService.class, new MethodCallback<NetworkApiService, String>() {
                    @Override
                    public Observable<BaseResponse<String>> bindMethod(NetworkApiService service, RetrofitClient retrofitClient) {
                        return service.checkReg();
                    }
                }, new ApiDisposableObserver<String>() {
                    @Override
                    public void onResult(String result, int status) {
                        if (callback != null) {
                            callback.onResultCallback(status);
                        }
                    }
                });
        return disposableObserver;
    }
}
