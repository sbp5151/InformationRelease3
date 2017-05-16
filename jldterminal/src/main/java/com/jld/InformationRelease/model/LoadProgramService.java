package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 9:49
 */
public interface LoadProgramService {

    /**
     * 节目加载
     * @param programId
     * @return
     */
    @POST(URLConstant.LOAD_PROGRAM_URL)
    Observable<ProgramResponseBean> loadProgram(@Field("programId") String programId);

}
