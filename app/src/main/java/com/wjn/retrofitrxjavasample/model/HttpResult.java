package com.wjn.retrofitrxjavasample.model;

import java.io.Serializable;

/**
 * Created by wjn on 2016/7/4.
 * 请求数据结果基类
 */
public class HttpResult<T> implements Serializable {


    private int code;//错误状态
    private String info = "";//错误信息

    private T data;//数据模板data

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("code=" + code + " ,info=" + info);
        if (null != data) {
            sb.append(" data:" + data.toString());
        }
        return sb.toString();
    }
}
