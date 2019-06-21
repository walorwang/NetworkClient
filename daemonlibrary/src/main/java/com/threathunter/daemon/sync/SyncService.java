package com.threathunter.daemon.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon.sync
 * @ClassName:      SyncService
 * @Description:    此服务需能交给操作系统使用
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 11:03
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 11:03
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class SyncService extends Service {

    // Storage for an instance of the sync adapter
    private static SyncAdapter sSyncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    public SyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}