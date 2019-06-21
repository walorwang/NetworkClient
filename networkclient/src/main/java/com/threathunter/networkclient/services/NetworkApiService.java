package com.threathunter.networkclient.services;

import com.threathunter.networkclient.base.BaseResponse;
import com.threathunter.networkclient.http.constants.NetworkConstants;

import io.reactivex.Observable;
import retrofit2.http.*;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.privacysecurity.services
 * @ClassName:      ApiService
 * @Description:    接口 url 服务类
 * @Author:         walorwang
 * @CreateDate:     2019/3/21 10:07
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/21 10:07
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public interface NetworkApiService {

    // 根据uuid获取token（url 使用配置文件管理）
    @POST(NetworkConstants.URL_CHECK_REG)
    Observable<BaseResponse<String>> checkReg();
}
