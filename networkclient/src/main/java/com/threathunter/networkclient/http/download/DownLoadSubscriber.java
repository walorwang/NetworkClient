package com.threathunter.networkclient.http.download;

import io.reactivex.observers.DisposableObserver;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.http.download
 * @ClassName:      DownLoadSubscriber
 * @Description:    java类作用描述
 * @Author:         walorwang
 * @CreateDate:     2019/3/27 13:31
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/27 13:31
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class DownLoadSubscriber<T> extends DisposableObserver<T> {
    private ProgressCallBack fileCallBack;

    public DownLoadSubscriber(ProgressCallBack fileCallBack) {
        this.fileCallBack = fileCallBack;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (fileCallBack != null)
            fileCallBack.onStart();
    }

    @Override
    public void onComplete() {
        if (fileCallBack != null)
            fileCallBack.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        if (fileCallBack != null)
            fileCallBack.onError(e);
    }

    @Override
    public void onNext(T t) {
        if (fileCallBack != null)
            fileCallBack.onSuccess(t);
    }
}