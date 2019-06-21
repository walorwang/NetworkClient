package com.threathunter.networkclient.binding.command;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.binding.command
 * @ClassName:      BindingConsumer
 * @Description:    A one-argument action
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:37
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:37
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public interface BindingConsumer<T> {
    void call(T t);
}
