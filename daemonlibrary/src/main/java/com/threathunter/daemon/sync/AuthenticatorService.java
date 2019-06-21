package com.threathunter.daemon.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon.sync
 * @ClassName:      AuthenticatorService
 * @Description:    授权此服务提供给SyncAdapter framework，用于调用Authenticator的方法
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 11:02
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 11:02
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;
    public AuthenticatorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}