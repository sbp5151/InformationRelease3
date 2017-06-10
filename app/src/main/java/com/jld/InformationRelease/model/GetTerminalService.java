package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.request_bean.UpdateTerminalRequest;
import com.jld.InformationRelease.bean.response_bean.GetTerminalResponse;
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
 * @create-time 2017/5/11 14:41
 */
public interface GetTerminalService {

    /**
     * 获取绑定的所有终端设备
     */
    @POST(URLConstant.GET_DEVICE_URL)
    Observable<GetTerminalResponse> getTerminal(@Body UpdateTerminalRequest body);
}
