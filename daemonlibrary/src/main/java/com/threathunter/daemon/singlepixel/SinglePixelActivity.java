package com.threathunter.daemon.singlepixel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.threathunter.daemon.WatchDogService;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon.singlepixel
 * @ClassName:      SinglePixelActivity
 * @Description:    该Activity的View只要设置为1像素然后设置在Window对象上即可。
 *                  在Activity的onDestroy周期中进行保活服务的存活判断从而唤醒服务。
 *                  运行在:watch进程, 为了提高watch进程的优先级 oom_adj值越小，优先级越高。
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 11:01
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 11:01
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class SinglePixelActivity extends Activity {

    private static final String TAG = SinglePixelActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window mWindow = getWindow();
        mWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attrParams = mWindow.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 1;
        attrParams.width = 1;
        mWindow.setAttributes(attrParams);
        ScreenManager.getInstance(this).setSingleActivity(this);
    }

    @Override
    protected void onDestroy() {
//        if (!SystemUtils.isAppAlive(this, Constant.PACKAGE_NAME)) {
        Log.d("wsh-daemon", " 1 像素Activity --- onDestroy");
            Intent intentAlive = new Intent(this, WatchDogService.class);
            startService(intentAlive);
//        }
        super.onDestroy();
    }
}
