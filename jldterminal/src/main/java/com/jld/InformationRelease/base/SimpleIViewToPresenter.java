package com.jld.InformationRelease.base;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/16 11:23
 */
public interface SimpleIViewToPresenter<T> {

    /**
     * 基础的请求的返回
     *
     * @param data
     * @param requestTag 请求标识
     */
    void loadDataSuccess(T data, int requestTag);

    /**
     * 基础请求的错误
     *
     * @param e
     * @param requestTag 请求标识
     */
    void loadDataError(Throwable e, int requestTag);
}
