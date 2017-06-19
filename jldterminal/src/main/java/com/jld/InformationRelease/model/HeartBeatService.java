package com.jld.InformationRelease.model;

import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/1 13:54
 */
public interface HeartBeatService {


    /**
     * 发送心跳包
     *
     * @param deviceId
     * @return
     */
    @POST(URLConstant.HEART_BEAT)
    @FormUrlEncoded
    Observable<BaseResponse> send_heart_beat(
            @Field("device_id") String deviceId
    );
}
