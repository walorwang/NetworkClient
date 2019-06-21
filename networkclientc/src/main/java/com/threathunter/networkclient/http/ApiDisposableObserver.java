package com.threathunter.networkclient.http;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.logger.Logger;
import com.threathunter.networkclient.base.BaseResponse;
import com.threathunter.networkclient.base.ResultCallback;
import com.threathunter.networkclient.manager.TokenManager;

import io.reactivex.observers.DisposableObserver;

public abstract class ApiDisposableObserver<T> extends DisposableObserver<T> {
    // resultCallback: token失效等必须走回调重新请求
    private ResultCallback resultCallback;

    public abstract void onResult(T t, int status);

    public void onResultWithErrorMessage(int status, String message) {}

    @Override
    public void onComplete() {

    }

    public ApiDisposableObserver() {}

    public ApiDisposableObserver(ResultCallback resultCallback) {
        /**
         * @method ApiDisposableObserver
         * @description 描述一下方法的作用
         * @date: 2019/3/28 12:49
         * @author: walorwang
         * @param [resultCallback 可传null]
         * @return
         */
        this.resultCallback = resultCallback;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof ResponseThrowable) {
            ResponseThrowable throwable = (ResponseThrowable) e;
            String message = throwable.message;
            if (!StringUtils.isTrimEmpty(message)) {
                ToastUtils.showShort(throwable.message);
            }
            return;
        }
        // 其他全部甩锅网络异常
        ToastUtils.showShort("网络异常");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!NetworkUtils.isConnected()) {
            onComplete();
        }
    }

    @Override
    public void onNext(Object o) {
        BaseResponse<T> baseResponse = (BaseResponse<T>) o;
        if (ObjectUtils.isEmpty(baseResponse)) {
            return;
        }

        int status = baseResponse.getStatus();
        switch (status) {
            case CodeRule.CODE_200:
            case CodeRule.CODE_201:
            case CodeRule.CODE_202:
            case CodeRule.CODE_204:
            case CodeRule.CODE_220:
                // 请求成功, 正确的操作方式
                T result = baseResponse.getResult();
                onResult(result, status);
                break;
            case CodeRule.CODE_300:
                // 请求失败，不打印Message
                Logger.d("请求失败" + status);
                break;
            case CodeRule.CODE_330:
                // 请求失败，打印Message
                Logger.d(baseResponse.getMessage());
                break;
            case CodeRule.CODE_401:
                TokenManager.getInstance().loadToken(resultCallback);
                break;
            case CodeRule.CODE_500:
            case CodeRule.CODE_504:
                // 服务器内部异常
                ToastUtils.showShort("服务器内部错误", status);
                break;
            default:
                Logger.d(baseResponse.getMessage());
                break;
        }

        // 接口错误时，返回错误信息
        String code = status + "";
        if (!code.startsWith("2")) {
            onResultWithErrorMessage(status, baseResponse.getMessage());
        }
    }

    public static final class CodeRule {
        //请求成功, 正确的操作方式
        public static final int CODE_200 = 200;
        public static final int CODE_201 = 201;
        public static final int CODE_202 = 202;
        // 删除成功
        public static final int CODE_204 = 204;
        //请求成功, 消息提示
        public static final int CODE_220 = 220;

        //请求失败，不打印Message
        public static final int CODE_300 = 300;
        //请求失败，打印Message
        public static final int CODE_330 = 330;

        // token失效
        public static final int CODE_401 = 401;

        //服务器内部异常
        public static final int CODE_500 = 500;
        public static final int CODE_504 = 504;
    }
}