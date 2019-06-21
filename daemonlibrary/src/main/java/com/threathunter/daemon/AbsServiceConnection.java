package com.threathunter.daemon;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon
 * @ClassName:      AbsServiceConnection
 * @Description:    java类作用描述
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 11:04
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 11:04
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
abstract class AbsServiceConnection implements ServiceConnection {

    // 当前绑定的状态
    boolean mConnectedState = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mConnectedState = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mConnectedState = false;
        onDisconnected(name);
    }

    @Override
    public void onBindingDied(ComponentName name) {
        onServiceDisconnected(name);
    }

    public abstract void onDisconnected(ComponentName name);
}
