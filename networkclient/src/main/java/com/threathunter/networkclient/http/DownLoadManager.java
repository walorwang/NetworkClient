package com.threathunter.networkclient.http;

import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.threathunter.networkclient.http.download.DownLoadSubscriber;
import com.threathunter.networkclient.http.download.ProgressCallBack;
import com.threathunter.networkclient.utils.HttpsUtils;
import com.threathunter.networkclient.utils.RetrofitFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 *
 * @ProjectName: PrivacySecurity
 * @Package: com.threathunter.networkclient.http
 * @ClassName: DownLoadManager
 * @Description: 文件下载管理，封装一行代码实现下载
 * @Author: walorwang
 * @CreateDate: 2019/3/20 18:54
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/3/20 18:54
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
public class DownLoadManager {
    private static DownLoadManager instance;

    private DownLoadManager() {
    }

    public static DownLoadManager getInstance() {
        if (instance == null) {
            instance = new DownLoadManager();
        }
        return instance;
    }

    public void load(String downUrl, final ProgressCallBack callBack) {
        /**
         * @method load
         * @description 下载
         * @date: 2019/3/20 18:54
         * @author: walorwang
         * @param [downUrl, callBack]
         * @return void
         */
        RetrofitFactory.getInstance()
                .creatClient(null, true)
                .create(DownloadApiService.class)
                .download(downUrl)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io()) //指定线程保存文件
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        callBack.saveFile(responseBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(new DownLoadSubscriber<>(callBack));
    }

    private interface DownloadApiService {
        @GET
        Observable<ResponseBody> download(@Url String url);
    }

    public interface IFileDownloaderListener {
        void onStart();
        void onProgress(int progress);
        void onComplete(File fileName);
        void onFailed();
    }

    /**
     * 下载文件
     * @param fileUrl 文件url
     * @param destFileDir 存储目标目录
     */
    public static void downloadFile(String fileUrl, final String destFileDir, final IFileDownloaderListener listener) {
        final File file = new File(destFileDir);
        if (listener != null) {
            listener.onStart();
        }
//        if (file.exists()) {
//            successCallBack(file, listener);
//            return;
//        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new HttpLoggingInterceptor())
                .build();
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("aaaaa", e.toString());
                failedCallBack("下载失败", listener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e("aaaaa", "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e("aaaaa", "current------>" + current);
                        progressCallBack((int) ((double) current / (double) total), listener);
                    }
                    fos.flush();
                    successCallBack(file, listener);
                } catch (IOException e) {
                    Log.e("aaaaa", e.toString());
                    e.printStackTrace();
                    failedCallBack("下载失败", listener);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e("aaaaa", e.toString());
                    }
                }
            }
        });
    }

    private static void successCallBack(File file, IFileDownloaderListener listener) {
        if (listener != null) {
            listener.onComplete(file);
        }
    }

    private static void failedCallBack(String file, IFileDownloaderListener listener) {
        if (listener != null) {
            listener.onFailed();
        }
    }

    private static void progressCallBack(int progress, IFileDownloaderListener listener) {
        if (listener != null) {
            listener.onFailed();
        }
    }
}
