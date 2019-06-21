package com.threathunter.networkclient.http.constants;

public interface NetworkConstants {
    // 服务端根路径
    String BASE = "http://39.108.221.167:8090";
    String BASE_ONLINE = "https://119.23.181.227";
    String BASEURL = "http://39.108.221.167:8090/api/v1/";
//    String BASEURL = "https://119.23.181.227/api/v1/";
    String BASEURL_ONLINE = "https://119.23.181.227/api/v1/";

    String URL_UPGRADE_APP = "app/info/check";

    // 网络请求方法
    String METHOD_GET = "GET";
    String METHOD_POST = "POST";
    String METHOD_PUT = "PUT";
    String METHOD_DELETE = "DELETE";

    // 公用token（获取）
    String URL_CHECK_REG = "user/uuid/check-reg";

    // 公用key（如token）
    String KEY_TOKEN = "token";
    String KEY_AUTHOR = "authorization";
    String KEY_UUID = "uuid";

    // "====="
    String KEY_EQUAL_LINE = "======";
    String KEY_COMMA = ",";
    String KEY_DASH = "-";
    String TAG = "retrofit";

    interface Path {
        // 接口下载目录的名字
        String DOWNLOAD_DIR_NAME = "downloaddir";
    }

    interface Server {
        String STATUS = "status";
        String MESSAGE = "message";
    }
}
