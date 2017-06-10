package com.jld.InformationRelease.model;

import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.request_bean.BindingRequest;
import com.jld.InformationRelease.bean.request_bean.ShowdownRestartRequestBean;
import com.jld.InformationRelease.bean.request_bean.TimeShowdownRequestBean;
import com.jld.InformationRelease.bean.request_bean.VolumeAdjustRequestBean;
import com.jld.InformationRelease.bean.response_bean.UpdateProgramResponse;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/20 14:35
 */
public interface TerminalFunctionService {

    /**
     * 设备绑定
     *
     * @param body
     * @return
     */
    @POST(URLConstant.BIND_DEVICE)
    Observable<BaseResponse> binding(@Body BindingRequest body);

    /**
     * 节目推送
     *
     * @param body
     * @return
     */
    @POST(URLConstant.PUSH_PROGRAM_URL)
    Observable<UpdateProgramResponse> push(@Body ProgramBean body);

    /**
     * 关机、重启
     *
     * @param body
     * @return
     */
    @POST(URLConstant.SHOWDOWN_RESTART_URL)
    Observable<BaseResponse> showdown_restart(@Body ShowdownRestartRequestBean body);

    /**
     * 定时开关机
     *
     * @param body
     * @return
     */
    @POST(URLConstant.TIME_POWER_ON_OFF_URL)
    Observable<BaseResponse> time_showdown(@Body TimeShowdownRequestBean body);

    /**
     * 音量调节
     *
     * @param body
     * @return
     */
    @POST(URLConstant.VOLUME_ADJUST_URL)
    Observable<BaseResponse> volume_adjust(@Body VolumeAdjustRequestBean body);


}
