package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.response_bean.ProgramLoadStateResponse;
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
 * @create-time 2017/7/4 10:06
 */
public interface ProgramLoadStateService {


    @FormUrlEncoded
    @POST(URLConstant.PROGRAM_LAOD_STATE)
    Observable<ProgramLoadStateResponse> loadState(
            @Field("programId") String programId,
            @Field("sign") String sign
    );
}
