package com.threathunter.networkclient.base;

/**
 * @ProjectName: PrivacySecurity
 * @Package: com.threathunter.networkclient.http
 * @ClassName: BaseResponse
 * @Description: 服务器响应的固定字段
 * @Author: walorwang
 * @CreateDate: 2019/3/20 18:55
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/3/20 18:55
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
public class BaseResponse<T> {

    private int status;
    private String message;
    private T result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
