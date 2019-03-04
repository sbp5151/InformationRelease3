package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.request.GetDevIdRequest;
import com.jld.InformationRelease.bean.response.GetDevIdResponse;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by boping on 2017/8/10.
 */

public interface GetDevIdService {
    /**
     * 根据mac地址获取设备ID
     *  @param body
     * @return
     */
    @POST(URLConstant.GET_DEV_ID)
    Observable<GetDevIdResponse> get_dev_id(@Body GetDevIdRequest body);


}
