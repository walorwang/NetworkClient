package com.threathunter.networkclient.http.cookie.store;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

import java.util.List;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.http.cookie.store
 * @ClassName:      CookieStore
 * @Description:    java类作用描述
 * @Author:         walorwang
 * @CreateDate:     2019/3/27 13:37
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/27 13:37
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public interface CookieStore {

    /** 保存url对应所有cookie */
    void saveCookie(HttpUrl url, List<Cookie> cookie);

    /** 保存url对应所有cookie */
    void saveCookie(HttpUrl url, Cookie cookie);

    /** 加载url所有的cookie */
    List<Cookie> loadCookie(HttpUrl url);

    /** 获取当前所有保存的cookie */
    List<Cookie> getAllCookie();

    /** 获取当前url对应的所有的cookie */
    List<Cookie> getCookie(HttpUrl url);

    /** 根据url和cookie移除对应的cookie */
    boolean removeCookie(HttpUrl url, Cookie cookie);

    /** 根据url移除所有的cookie */
    boolean removeCookie(HttpUrl url);

    /** 移除所有的cookie */
    boolean removeAllCookie();
}
