package com.wjn.retrofitrxjavasample.model;

import android.content.Context;
import android.util.MalformedJsonException;

import com.google.gson.JsonSyntaxException;
import com.wjn.retrofitrxjavasample.network.ApiException;
import com.wjn.retrofitrxjavasample.network.SubscriberOnNextListener;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by wjn on 2016/7/4.
 * 自定义网络请求观察者
 */
public class HttpSubscriber<T> extends Subscriber<T> {

    private SubscriberOnNextListener mSubscriberOnNextListener;
    public HttpSubscriber(SubscriberOnNextListener mSubscriberOnNextListener) {
        //把SubscriberOnNextListener对象传进来做统一调用
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onStart();
        }
    }
    @Override
    public void onCompleted() {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onCompleted();
        }
    }
    @Override
    public void onError(Throwable e) {

        if (mSubscriberOnNextListener != null) {
            if (e instanceof SocketTimeoutException ||e instanceof ConnectException ||e instanceof UnknownHostException
                    ){
                e.printStackTrace();
                mSubscriberOnNextListener.onNetworkError(e);
            }else if(e instanceof MalformedJsonException ||e instanceof JsonSyntaxException){
                e.printStackTrace();
                mSubscriberOnNextListener.onParseError(e);
            }else if(e instanceof ApiException){
                ApiException apiException = (ApiException) e;
                mSubscriberOnNextListener.onApiError(apiException);
            }else {
                mSubscriberOnNextListener.onOtherError(e);
                e.printStackTrace();
            }

        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 取消对observable的订阅
     */
    public void unSubscribe() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}
