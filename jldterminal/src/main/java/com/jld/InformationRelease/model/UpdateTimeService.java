package com.jld.InformationRelease.model;

import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.request.UpdateTimeRequest;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by boping on 2017/8/10.
 */

public interface UpdateTimeService {
    /**
     * 更新设备在线时间
     *
     * @param body
     * @return
     */
    @POST(URLConstant.UPDATE_TIME)
    Observable<BaseResponse> update_time(@Body UpdateTimeRequest body);


}
