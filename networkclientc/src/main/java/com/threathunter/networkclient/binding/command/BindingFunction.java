package com.threathunter.networkclient.binding.command;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.binding.command
 * @ClassName:      BindingFunction
 * @Description:    Represents a function with zero arguments
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:38
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:38
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public interface BindingFunction<T> {
    T call();
}
