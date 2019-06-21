package com.threathunter.daemon.singlepixel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon.singlepixel
 * @ClassName:      ScreenReceiverUtil
 * @Description:    对广播进行监听，封装为一个ScreenReceiverUtil类，进行锁屏解锁的广播动态注册监听
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 10:59
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 10:59
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class ScreenReceiverUtil {
    private Context mContext;
    private SreenBroadcastReceiver mScreenReceiver;
    private SreenStateListener mStateReceiverListener;

    public ScreenReceiverUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void setScreenReceiverListener(SreenStateListener mStateReceiverListener) {
        this.mStateReceiverListener = mStateReceiverListener;
        // 动态启动广播接收器
        this.mScreenReceiver = new SreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public void stopScreenReceiverListener() {
        mContext.unregisterReceiver(mScreenReceiver);
    }

    /**
     * 监听screen状态对外回调接口
     */
    public interface SreenStateListener {
        void onSreenOn();

        void onSreenOff();

        void onUserPresent();
    }

    public class SreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mStateReceiverListener == null) {
                return;
            }
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                mStateReceiverListener.onSreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mStateReceiverListener.onSreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                mStateReceiverListener.onUserPresent();
            }
        }
    }
}
