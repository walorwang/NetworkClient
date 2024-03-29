package com.threathunter.daemon;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.threathunter.basecomponent.util.NotificationUtil;

/**
 * 主要Service 用户继承该类用来处理自己业务逻辑
 * 该类已经实现如何启动结束及保活的功能，用户无需关心。
 */
public abstract class AbsWorkService extends Service {

    protected static final int HASH_CODE = 1;
    private StopBroadcastReceiver stopBroadcastReceiver;

    private AbsServiceConnection mConnection = new AbsServiceConnection() {

        @Override
        public void onDisconnected(ComponentName name) {
            Boolean shouldStopService = shouldStopService(null, 0, 0);
            DaemonEnv.startServiceMayBind(AbsWorkService.this, WatchDogService.class, mConnection, shouldStopService);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("wsh-daemon", "AbsWorkService  onCreate 启动。。。。");

        startRegisterReceiver();
        //启动前台服务而不显示通知的漏洞已在 API Level 25 修复，大快人心！
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            //利用漏洞在 API Level 17 及以下的 Android 系统中，启动前台服务而不显示通知
            NotificationCompat.Builder builder = NotificationUtil.createForegroundNotification(this);
            startForeground(HASH_CODE, builder.build());
            //利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Boolean shouldStopService = shouldStopService(null, 0, 0);
                DaemonEnv.startServiceSafely(AbsWorkService.this,
                        WorkNotificationService.class,
                        shouldStopService);
            }
        }
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), WatchDogService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return onStart(intent, flags, startId);
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
//        onStart(intent, 0, 0);
        return onBindService(intent, null);
    }

    protected void onEnd(Intent rootIntent) {
        onServiceKilled(rootIntent);
        // // 不同的进程，所有的静态和单例都会失效
        Boolean shouldStopService = shouldStopService(null, 0, 0);
        if (shouldStopService) {
            return;
        }
        Log.d("wsh-daemon", "onEnd ----  搞事 + onDestroy  ：" + shouldStopService);
//        DaemonEnv.startServiceMayBind(AbsWorkService.this,WatchDogService.class,mConnection,shouldStopService);
        DaemonEnv.startServiceSafely(AbsWorkService.this, WatchDogService.class, false);
    }

    /**
     * 最近任务列表中划掉卡片时回调
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("wsh-daemon", "onEnd ----  搞事 + onTaskRemoved  ：");
        onEnd(rootIntent);
    }

    /**
     * 设置-正在运行中停止服务时回调
     */
    @Override
    public void onDestroy() {
        Log.d("wsh-daemon", "onEnd ----  搞事 + onDestroy  ：");
        onEnd(null);
        startUnRegisterReceiver();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    public abstract Boolean shouldStopService(@Nullable Intent intent, int flags, int startId);
    public abstract void startWork(Intent intent, int flags, int startId);

    public abstract void stopWork(@Nullable Intent intent, int flags, int startId);
    /**
     * 任务是否正在运行? 由实现者处理
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    public abstract Boolean isWorkRunning(Intent intent, int flags, int startId);

    @NonNull
    public abstract IBinder onBindService(Intent intent, Void alwaysNull);
    public abstract void onServiceKilled(Intent rootIntent);

    /**
     * 1.防止重复启动，可以任意调用 DaemonEnv.startServiceMayBind(Class serviceClass);
     * 2.利用漏洞启动前台服务而不显示通知;
     * 3.在子线程中运行定时任务，处理了运行前检查和销毁时保存的问题;
     * 4.启动守护服务;
     * 5.守护 Service 组件的启用状态, 使其不被 MAT 等工具禁用.
     */
    protected int onStart(Intent intent, int flags, int startId) {

        //启动守护服务，运行在:watch子进程中
        Boolean shouldStopService = shouldStopService(null, 0, 0);
        DaemonEnv.startServiceMayBind(AbsWorkService.this, WatchDogService.class, mConnection, shouldStopService);

        //业务逻辑: 实际使用时，根据需求，将这里更改为自定义的条件，判定服务应当启动还是停止 (任务是否需要运行)
        if (shouldStopService) {
            // 此处不必重复关闭服务。否则mConnection.mConnectedState的状态没有来得及改变，
            //  再次unbindService(conn)服务会导致 Service not registered 异常抛出。 服务启动和关闭都需要耗时，短时间内不宜频繁开启和关闭。
            // stopService(intent, flags, startId); 
        } else {
            startService(intent, flags, startId);
        }

        return START_STICKY;
    }

    void startService(Intent intent, int flags, int startId) {
        //若还没有取消订阅，说明任务仍在运行，为防止重复启动，直接 return
        Boolean workRunning = isWorkRunning(intent, flags, startId);
        if (workRunning != null && workRunning) {
            return;
        }
        //业务逻辑
        startWork(intent, flags, startId);
    }

    /**
     * 任务完成，停止服务并取消定时唤醒
     * 停止服务使用取消订阅的方式实现，而不是调用 Context.stopService(Intent name)。因为：
     * 1.stopService 会调用 Service.onDestroy()，而 AbsWorkService 做了保活处理，会把 Service 再拉起来；
     * 2.我们希望 AbsWorkService 起到一个类似于控制台的角色，即 AbsWorkService 始终运行 (无论任务是否需要运行)，
     * 而是通过 onStart() 里自定义的条件，来决定服务是否应当启动或停止。
     */
    private void stopService(Intent intent, int flags, int startId) {
        //取消对任务的订阅

        startUnRegisterReceiver();

        // 给实现者处理业务逻辑
        stopWork(intent, flags, startId);

        if (mConnection.mConnectedState) {
            unbindService(mConnection);
        }

        exit();
    }

    private void exit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
                System.exit(0);
            }
        }, 3000);
    }

    public static class WorkNotificationService extends Service {

        /**
         * 利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            NotificationCompat.Builder builder = NotificationUtil.createForegroundNotification(this);

            startForeground(AbsWorkService.HASH_CODE, builder.build());
            stopSelf();
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    private void startRegisterReceiver() {
        if (stopBroadcastReceiver == null) {
            stopBroadcastReceiver = new StopBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DaemonEnv.ACTION_CANCEL_JOB_ALARM_SUB);
            registerReceiver(stopBroadcastReceiver, intentFilter);
        }
    }

    private void startUnRegisterReceiver() {
        if (stopBroadcastReceiver != null) {
            unregisterReceiver(stopBroadcastReceiver);
            stopBroadcastReceiver = null;
        }
    }

    class StopBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 停止业务
            stopService(null, 0, 0);
        }
    }
}
