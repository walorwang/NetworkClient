## 实现网络请求方法1 Demo

```
RetrofitFactory.getInstance()
        .creatClient()
        .create(ApiService.class, new MethodCallback<ApiService, String>() {
            @Override
            public Observable<BaseResponse<String>> bindMethod(ApiService service, RetrofitClient retrofitClient) {
                return service.testPost();
            }
        }, new ApiDisposableObserver<String>() {
            @Override
            public void onResult(String s) {

            }
        });
```

## 实现网络请求方法2 Demo
```
public void requestNetWork() {
    RetrofitFactory.getInstance()
            .creatClient()
            .create(ApiService.class)
            .demoGet()
            .compose(RxThreadTransform.io2main()) //线程调度
            .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
            .doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable disposable) throws Exception {
                    showDialog("正在请求...");
                }
            })
            .subscribe(new Consumer<BaseResponse<DemoEntity>>() {
                @Override
                public void accept(BaseResponse<DemoEntity> response) throws Exception {
                    itemIndex = 0;
                    //清除列表
                    observableList.clear();
                    //请求成功
                    if (response.getCode() == 1) {
                        //将实体赋给LiveData
                        for (DemoEntity.ItemsEntity entity : response.getResult().getItems()) {
                            NetWorkItemViewModel itemViewModel = new NetWorkItemViewModel(NetWorkViewModel.this, entity);
                            //双向绑定动态添加Item
                            observableList.add(itemViewModel);
                        }
                    } else {
                        //code错误时也可以定义Observable回调到View层去处理
                        ToastUtils.showShort("数据错误");
                    }
                }
            }, new Consumer<ResponseThrowable>() {
                @Override
                public void accept(ResponseThrowable throwable) throws Exception {
                    //关闭对话框
                    dismissDialog();
                    //请求刷新完成收回
                    uc.finishRefreshing.set(!uc.finishRefreshing.get());
                    ToastUtils.showShort(throwable.message);
                    throwable.printStackTrace();
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    //关闭对话框
                    dismissDialog();
                    //请求刷新完成收回
                    uc.finishRefreshing.set(!uc.finishRefreshing.get());
                }
            });
}
```

## 网络请求下载 Demo
```
    private void downFile(String url) {
        String destFileDir = context.getCacheDir().getPath();
        String destFileName = System.currentTimeMillis() + ".apk";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        DownLoadManager.getInstance().load(url, new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onCompleted() {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(ResponseBody responseBody) {
                ToastUtils.showShort("文件下载完成！");
            }

            @Override
            public void progress(final long progress, final long total) {
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) progress);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtils.showShort("文件下载失败！");
                progressDialog.dismiss();
            }
        });
    }
```