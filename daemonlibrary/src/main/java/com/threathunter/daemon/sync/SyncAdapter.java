package com.threathunter.daemon.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.threathunter.daemon.DaemonEnv;
import com.threathunter.daemon.WatchDogService;
import com.threathunter.daemon.WatchProcessPrefHelper;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon.sync
 * @ClassName:      SyncAdapter
 * @Description:    java类作用描述
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 11:05
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 11:05
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        DaemonEnv.startServiceSafely(mContext, WatchDogService.class,
                !WatchProcessPrefHelper.getIsStartDaemon(mContext));
    }
}