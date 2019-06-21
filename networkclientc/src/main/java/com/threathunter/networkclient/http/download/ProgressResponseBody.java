package com.threathunter.networkclient.http.download;

import com.threathunter.networkclient.bus.RxBus;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;

import java.io.IOException;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.networkclient.http.download
 * @ClassName:      ProgressResponseBody
 * @Description:    java类作用描述
 * @Author:         walorwang
 * @CreateDate:     2019/3/27 13:37
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/27 13:37
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class ProgressResponseBody extends ResponseBody {
    private ResponseBody responseBody;

    private BufferedSource bufferedSource;
    private String tag;

    public ProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    public ProgressResponseBody(ResponseBody responseBody, String tag) {
        this.responseBody = responseBody;
        this.tag = tag;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                //使用RxBus的方式，实时发送当前已读取(上传/下载)的字节数据
                RxBus.getDefault().post(new DownLoadStateBean(contentLength(), bytesReaded, tag));
                return bytesRead;
            }
        };
    }
}
