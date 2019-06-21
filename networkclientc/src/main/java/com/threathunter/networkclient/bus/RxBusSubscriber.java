package com.threathunter.networkclient.bus;


import io.reactivex.observers.DisposableObserver;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.bus
 * @ClassName:      RxBusSubscriber
 * @Description:    为RxBus使用的Subscriber, 主要提供next事件的try,catch
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:42
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:42
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public abstract class RxBusSubscriber<T> extends DisposableObserver<T> {

    @Override
    public void onNext(T t) {
        try {
            onEvent(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    protected abstract void onEvent(T t);
}
