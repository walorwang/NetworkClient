package com.threathunter.networkclient.bus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.bus
 * @ClassName:      RxSubscriptions
 * @Description:    管理 CompositeSubscription
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:42
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:42
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class RxSubscriptions {
    private static CompositeDisposable mSubscriptions = new CompositeDisposable ();

    public static boolean isDisposed() {
        return mSubscriptions.isDisposed();
    }

    public static void add(Disposable s) {
        if (s != null) {
            mSubscriptions.add(s);
        }
    }

    public static void remove(Disposable s) {
        if (s != null) {
            mSubscriptions.remove(s);
        }
    }

    public static void clear() {
        mSubscriptions.clear();
    }

    public static void dispose() {
        mSubscriptions.dispose();
    }

}
