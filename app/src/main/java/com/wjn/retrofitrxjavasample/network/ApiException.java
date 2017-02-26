package com.wjn.retrofitrxjavasample.network;


import com.wjn.retrofitrxjavasample.model.HttpResult;

/**
 * Created by wjn on 2016/7/4.
 * 自定义API异常
 */
public class ApiException extends RuntimeException {


    private int status;
    private HttpResult mHttpResult;


    public ApiException(HttpResult httpResult){
        this(getApiExceptionMessage(httpResult));
        this.mHttpResult = httpResult;
    }

    public ApiException(String errorInfo){
        super(errorInfo);
    }

    /**
     * 把服务器的错误信息和错误码返回给前端
     * @param httpResult
     * @return
     */
    private static String getApiExceptionMessage(HttpResult httpResult){
        return  httpResult.getInfo()+"("+httpResult.getCode()+")";
    }

    public int getStatus() {
        return mHttpResult.getCode();
    }
}
