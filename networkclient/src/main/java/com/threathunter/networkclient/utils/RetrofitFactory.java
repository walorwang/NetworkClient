package com.threathunter.networkclient.utils;

public class RetrofitFactory {

    private static class Holder {
        private static final RetrofitFactory INSTANCE = new RetrofitFactory();
    }

    public static RetrofitFactory getInstance() {
        return Holder.INSTANCE;
    }

    public RetrofitClient creatClient() {
        return new RetrofitClient();
    }

    public RetrofitClient creatClient(String url, boolean isProgressInterceptor) {
        return new RetrofitClient(url, isProgressInterceptor);
    }
}
