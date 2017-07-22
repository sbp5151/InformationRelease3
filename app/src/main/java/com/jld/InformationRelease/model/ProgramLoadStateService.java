package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.request_bean.PushStateRequest;
import com.jld.InformationRelease.bean.response_bean.ProgramPushStateResponse;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/4 10:06
 */
public interface ProgramLoadStateService {


    @POST(URLConstant.PROGRAM_LAOD_STATE)
    Observable<ProgramPushStateResponse> loadState(
            @Body PushStateRequest body
    );
}
