package com.jld.InformationRelease.model;

import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.response_bean.FileResponseBean;
import com.jld.InformationRelease.util.URLConstant;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/25 11:11
 */
public interface FileService {

    /**
     * 单张上传
     *
     * @param
     * @return
     */
    @Multipart
    @POST(URLConstant.PUSH_FILES)
    Observable<BaseResponse> updateFile(
            @Part MultipartBody.Part file);

    /**
     * 多张上传
     *
     * @param
     * @return
     */
    @Multipart
    @POST(URLConstant.PUSH_FILES)
    Observable<FileResponseBean> updateFiles(
            @Part String sing,
            @PartMap Map<String, RequestBody> files);
}