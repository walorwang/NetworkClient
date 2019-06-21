package com.threathunter.daemon;

import android.content.Context;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon
 * @ClassName:      WatchProcessPrefHelper
 * @Description:    用于多进程通讯的 SharedPreferences
 *                  此处存在着风险  github --> PreferencesProvider 项目
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 11:05
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 11:05
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class WatchProcessPrefHelper {

    private static final String SHARED_UTILS = "watch_process";

    private static final String KEY_IS_START_DAEMON = "is_start_sport"; // 是否开始了一次保活（做为保活的判断依据）


    public static void setIsStartSDaemon(Context context,boolean mapType){
        context.getSharedPreferences(SHARED_UTILS, Context.MODE_MULTI_PROCESS).edit().putBoolean(KEY_IS_START_DAEMON, mapType).apply();
    }

    public static boolean getIsStartDaemon(Context context){
        return context.getSharedPreferences(SHARED_UTILS, Context.MODE_MULTI_PROCESS).getBoolean(KEY_IS_START_DAEMON, false);
    }


}
