package com.wjn.retrofitrxjavasample.network;

import com.wjn.retrofitrxjavasample.model.HttpResult;
import com.wjn.retrofitrxjavasample.model.IPInfo;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by wjn on 2017/2/21.
 * retrofit 请求服务
 */

public interface RequestService {

    /**
     * 查询IP请求
     * @param map
     * @return
     */
    @GET(Urls.QUERY_IP_URI)
    Observable<HttpResult<IPInfo>> getIPInfo(@QueryMap Map<String, String> map);
}
