package com.threathunter.networkclient.http.download;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.StringUtils;
import com.threathunter.networkclient.bus.RxBus;
import com.threathunter.networkclient.bus.RxSubscriptions;
import com.threathunter.networkclient.http.constants.NetworkConstants;

import java.io.File;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.http.download
 * @ClassName:      ProgressCallBack
 * @Description:    java类作用描述
 * @Author:         walorwang
 * @CreateDate:     2019/3/27 13:36
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/27 13:36
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public abstract class ProgressCallBack<T> {

    private String destFileName; // 文件名（用常量保存，方便后续读取完整路径）
    private Disposable mSubscription;

    public ProgressCallBack(String destFileName) {
        this.destFileName = destFileName;
        subscribeLoadProgress();
    }

    public abstract void onSuccess(T t);

    public void progress(long progress, long total){

    }

    public void onStart() {
    }

    public void onCompleted() {
    }

    public abstract void onError(Throwable e);

    public void saveFile(ResponseBody body) {
        if (StringUtils.isTrimEmpty(destFileName)){
            return;
        }

        // 固定下载目录（保存一次后，body 大小为0）
        File dir = new File(PathUtils.getInternalAppFilesPath(), NetworkConstants.Path.DOWNLOAD_DIR_NAME);
        FileUtils.createOrExistsDir(dir);
        File file = new File(dir, destFileName);
        String path = file.getAbsolutePath();
        FileUtils.createOrExistsFile(file);
        SPStaticUtils.put(destFileName, path);
        if (!ObjectUtils.isEmpty(body)) {
            InputStream is = body.byteStream();
            FileIOUtils.writeFileFromIS(file, is);
        }

        // 取消订阅
        unsubscribe();
    }

    /**
     * 订阅加载的进度条
     */
    public void subscribeLoadProgress() {
        mSubscription = RxBus.getDefault().toObservable(DownLoadStateBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
                .subscribe(new Consumer<DownLoadStateBean>() {
                    @Override
                    public void accept(final DownLoadStateBean progressLoadBean) throws Exception {
                        progress(progressLoadBean.getBytesLoaded(), progressLoadBean.getTotal());
                    }
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    public void unsubscribe() {
        RxSubscriptions.remove(mSubscription);
    }
}