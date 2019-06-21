package com.threathunter.networkclient.bus.event;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.bus.event
 * @ClassName:      SnackbarMessage
 * @Description:    A SingleLiveEvent used for Snackbar messages.
 *                  Like a {@link SingleLiveEvent} but also prevents null messages and uses a custom observer
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:40
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:40
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class SnackbarMessage extends SingleLiveEvent<Integer> {

    public void observe(LifecycleOwner owner, final SnackbarObserver observer) {
        super.observe(owner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer t) {
                if (t == null) {
                    return;
                }
                observer.onNewMessage(t);
            }
        });
    }

    public interface SnackbarObserver {
        /**
         * @method
         * @description Called when there is a new message to be shown
         * @date: 2019/3/20 18:41
         * @author: walorwang
         * @param snackbarMessageResourceId The new message, non-null
         * @return
         */
        void onNewMessage(@StringRes int snackbarMessageResourceId);
    }

}
