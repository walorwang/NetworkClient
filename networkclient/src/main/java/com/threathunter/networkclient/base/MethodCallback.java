package com.threathunter.networkclient.base;

import com.threathunter.networkclient.utils.RetrofitClient;
import io.reactivex.Observable;

/**
 * @ProjectName: PrivacySecurity
 * @Package: com.threathunter.networkclient.base
 * @ClassName: Callback
 * @Description:
 * @Author: walorwang
 * @CreateDate: 2019/3/27 15:51
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/3/27 15:51
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public interface MethodCallback<T, E> {

    /**
     *
     * @ProjectName:    PrivacySecurity
     * @Package:        com.threathunter.networkclient.base
     * @ClassName:      MethodCallback
     * @Description:    添加json请求参数等
     * @Author:         walorwang
     * @CreateDate:     2019/3/27 16:07
     * @UpdateUser:     更新者
     * @UpdateDate:     2019/3/27 16:07
     * @UpdateRemark:   更新内容
     * @Version:        1.0
     */
    Observable<BaseResponse<E>> bindMethod(T service, RetrofitClient retrofitClient);
}
