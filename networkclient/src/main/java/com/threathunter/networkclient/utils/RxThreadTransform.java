package com.threathunter.networkclient.utils;

import com.threathunter.networkclient.http.ExceptionHandle;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @ProjectName: PrivacySecurity
 * @Package: com.threathunter.basecomponent
 * @ClassName: RxThreadTransform
 * @Description: 创建rxjava转换线程的类
 * @Author: zhouchaoran
 * @CreateDate: 2019/3/27 上午11:25
 * @UpdateUser: zhouchaoran
 * @UpdateDate: 2019/3/27 上午11:25
 * @UpdateRemark:
 * @Version: v1.0
 */
public class RxThreadTransform {

    public static ObservableTransformer io2main() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static FlowableTransformer io2mainFlowable() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static ObservableTransformer exceptionTransformer() {

        return upstream -> upstream.onErrorResumeNext((Function<Throwable, ObservableSource>) throwable -> {
            return Observable.error(ExceptionHandle.handleException(throwable));
        });
    }

    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable t) {
            return Observable.error(t);
        }
    }
}
