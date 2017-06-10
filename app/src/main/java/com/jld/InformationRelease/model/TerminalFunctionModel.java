package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver;
import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.request_bean.BindingRequest;
import com.jld.InformationRelease.bean.request_bean.ShowdownRestartRequestBean;
import com.jld.InformationRelease.bean.request_bean.TimeShowdownRequestBean;
import com.jld.InformationRelease.bean.request_bean.VolumeAdjustRequestBean;
import com.jld.InformationRelease.bean.response_bean.UpdateProgramResponse;
import com.jld.InformationRelease.interfaces.IPresenterListen;
import com.jld.InformationRelease.util.RetrofitManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/20 14:39
 */
public class TerminalFunctionModel{

    private final Retrofit mRetrofit;
    private final TerminalFunctionService mTerminalFunctionService;
    private Context mContext;

    public TerminalFunctionModel(Context context) {
        mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mTerminalFunctionService = mRetrofit.create(TerminalFunctionService.class);
        mContext = context;
    }

    /**
     * 设备绑定
     *
     * @param body
     * @param callback
     * @param requestTag
     */
    public void retrofitBinding(BindingRequest body, final IPresenterListen<BaseResponse> callback, final int requestTag) {

        mTerminalFunctionService.binding(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<BaseResponse>(callback,requestTag));

    }

    /**
     * 节目推送
     *
     * @param body
     * @param callback
     * @param requestTag
     */
    public void retrofitPushProgram(ProgramBean body, final IPresenterListen<UpdateProgramResponse> callback, final int requestTag) {

        mTerminalFunctionService.push(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<UpdateProgramResponse>(callback,requestTag){
                    @Override
                    public void onNext(UpdateProgramResponse value) {
                        if (value != null && value.getResult().equals("0")) {//成功
                            callback.requestSuccess(value, requestTag);
                        } else if (value != null) {//失败
                            callback.requestError(new Exception(value.getMsg()), requestTag);
                        } else {//错误
                            callback.requestError(new Exception("获取数据失败"), requestTag);
                        }
                    }
                });
    }

    /**
     * 关机&重启
     *
     * @param body
     * @param callback
     * @param requestTag
     */
    public void retrofitShowdownRestart(ShowdownRestartRequestBean body, final IPresenterListen<BaseResponse> callback, final int requestTag) {

        mTerminalFunctionService.showdown_restart(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<BaseResponse>(callback,requestTag));

    }

    /**
     * 定时开关机
     *
     * @param body
     * @param callback
     * @param requestTag
     */
    public void retrofitTimeShowdown(TimeShowdownRequestBean body, final IPresenterListen<BaseResponse> callback, final int requestTag) {

        mTerminalFunctionService.time_showdown(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<BaseResponse>(callback,requestTag));

    }

    /**
     * 音量调节
     *
     * @param body
     * @param callback
     * @param requestTag
     */
    public void retrofitVolumeAdjust(VolumeAdjustRequestBean body, final IPresenterListen<BaseResponse> callback, final int requestTag) {

        mTerminalFunctionService.volume_adjust(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<BaseResponse>(callback,requestTag));
    }

}
