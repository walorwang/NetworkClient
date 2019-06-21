package com.threathunter.networkclient.http.cookie;

import com.threathunter.networkclient.http.cookie.store.CookieStore;

import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @ProjectName: PrivacySecurity
 * @Package: com.threathunter.networkclient.http.cookie
 * @ClassName: CookieJarImpl
 * @Description: CookieJar 实现类
 * @Author: walorwang
 * @CreateDate: 2019/3/27 13:10
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/3/27 13:10
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
public class CookieJarImpl implements CookieJar {

    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//        if (ObjectUtils.isEmpty(url) || StringUtils.isTrimEmpty(url.toString())) {
//            return;
//        }
//        String urlStr = url.toString();
//        if (urlStr.contains(NetworkConstants.URL_CHECK_REG)) {
//            String token = SPStaticUtils.getString(NetworkConstants.KEY_TOKEN);
//            if (!StringUtils.isTrimEmpty(token)) {
//                return;
//            }
//
//            // 请求token的接口 保存token
//            for (Cookie cookie : cookies) {
//                String name = cookie.name();
//                if (NetworkConstants.KEY_TOKEN.equalsIgnoreCase(name)) {
//                    SPStaticUtils.put(NetworkConstants.KEY_TOKEN, cookie.value(), true);
//                    break;
//                }
//            }
//        }
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return Collections.emptyList();
    }
}