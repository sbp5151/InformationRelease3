package com.jld.InformationRelease.bean.response;

import com.jld.InformationRelease.base.BaseResponse;

/**
 * Created by boping on 2017/8/12.
 */

public class GetDevIdResponse extends BaseResponse {

    private String id;

    public GetDevIdResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
