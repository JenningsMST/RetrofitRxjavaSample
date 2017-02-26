package com.wjn.retrofitrxjavasample.network;

import android.util.Log;

import com.wjn.retrofitrxjavasample.model.HttpResult;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wjn on 2017/2/25.
 */

public class HttpMethod {

    private static final int DEFAULT_TIMEOUT = 5;
    private static final int DEFAULT_UPLOAD_TIMEOUT = 30;
    private static volatile HttpMethod defaultHttpMethod;

    private Retrofit retrofit;
    private RequestService requestService;

    private HttpMethod() {

    }

    public static HttpMethod getInstance() {
        HttpMethod httpMethod = defaultHttpMethod;
        if (defaultHttpMethod == null) {
            synchronized (HttpMethod.class) {
                httpMethod = defaultHttpMethod;
                if (defaultHttpMethod == null) {
                    httpMethod = new HttpMethod();
                    defaultHttpMethod = httpMethod;
                }
            }
        }
        return httpMethod;
    }

    private void retrofitBuild() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(Urls.BASE_URL)
                .build();
        requestService = retrofit.create(RequestService.class);
    }

    /**
     * 观察者模式，把observable（被观察者）和subscriber (观察者)通过subscribe实现订阅关系
     * 由于网络请求是耗时任务，所以我们必须添加线程管理
     * @param observable
     * @param subscriber
     * @param <T>
     */
    public <T> void toSubscribe(Observable<T> observable, Subscriber<T> subscriber) {
        observable
                /*
                订阅关系发生在IO线程中,Schedulers叫调度器，subsribeOn( )操作符可以指定observable运行的线程
                而我设定的调度器的类型是Schedulers.io()。Schedulers.io()是 I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
                行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，
                可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。
                 */
                .subscribeOn(Schedulers.io())
                /*
                解除订阅关系也发生在IO线程中
                 */
                .unsubscribeOn(Schedulers.io())
                /*
                指定subscriber (观察者)的回调在主线程中，
                observeOn的作用是指定subscriber（观察者）将会在哪个Scheduler观察这个Observable,
                由于subscriber已经能取到界面所关心的数据了，所以设定指定subscriber的回调在主线程中
                 */
                .observeOn(AndroidSchedulers.mainThread())
                /*
                订阅观察者，subscribe就相当于setOnclickListener()
                 */
                .subscribe(subscriber);
        //subscribeOn影响的是它调用之前的代码（也就是observable），observeOn影响的是它调用之后的代码（也就是subscribe()）
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     * <p/>
     * Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>,T> {

        @Override
        public T call(HttpResult<T> httpResult) {

            Log.i("wjn","httpResult: "+httpResult.toString());
            if (httpResult.getCode()!=0){
                throw new ApiException(httpResult);
            }
            return httpResult.getData();
        }
    }

    /**
     * 查询IP请求
     *
     * @param subscriber
     * @param params
     */
    public void getIPInfo(Subscriber<HttpResult> subscriber, Map<String, String> params) {
        retrofitBuild();
        Observable observable = requestService.getIPInfo(params).map(new HttpResultFunc());
        toSubscribe(observable, subscriber);
    }
}
