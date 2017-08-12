package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.request.CheckBindRequest;
import com.jld.InformationRelease.bean.response.CheckBindResponse;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by boping on 2017/8/10.
 */

public interface CheckBindService {
    /**
     * 检查设备是否被绑定
     * @param body
     * @return
     */
    @POST(URLConstant.CHECK_BIND)
    Observable<CheckBindResponse> check_bind(@Body CheckBindRequest body);


}
