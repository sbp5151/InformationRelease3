package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.response_bean.CheckVersionResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.jld.InformationRelease.util.URLConstant.GET_VERSION_CODE;

/**
 * Created by boping on 2017/8/21.
 */

public interface CheckVersionUpdateService {

    /**
     * 获取最新版本和下载连接 检查版本更新
     *
     * @return
     */
    @GET(GET_VERSION_CODE)
    Observable<CheckVersionResponse> checkVersionUpdate(@Query("v") String version);
}
