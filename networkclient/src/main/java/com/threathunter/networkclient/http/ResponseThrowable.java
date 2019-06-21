package com.threathunter.networkclient.http;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.http
 * @ClassName:      ResponseThrowable
 * @Description:    Exception 封装类
 * @Author:         walorwang
 * @CreateDate:     2019/3/20 18:48
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/20 18:48
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class ResponseThrowable extends Exception {
    public int code;
    public String message;

    public ResponseThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }
}
