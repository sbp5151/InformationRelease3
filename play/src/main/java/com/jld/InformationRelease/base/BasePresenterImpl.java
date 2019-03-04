package com.jld.InformationRelease.base;

/**
 * 代理对象的基础实现
 *
 * @param <T> 业务请求返回的具体对象
 */
public class BasePresenterImpl<T> implements IPresenterToModel<T> {
    public IViewToPresenter iView;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public BasePresenterImpl(IViewToPresenter view) {
        this.iView = view;
    }

    @Override
    public void beforeRequest(int requestTag) {
        //显示LOading
        iView.showProgress(requestTag);
    }

    @Override
    public void requestError(Throwable e, int requestTag) {
        //通知UI具体的错误信息
        iView.loadDataError(e,requestTag);
    }

    @Override
    public void requestComplete(int requestTag) {
        //隐藏Loading
        iView.hideProgress(requestTag);
    }

    @Override
    public void requestSuccess(T callBack, int requestTag) {
        //将获取的数据回调给UI（activity或者fragment）
        iView.loadDataSuccess(callBack, requestTag);
    }
}
