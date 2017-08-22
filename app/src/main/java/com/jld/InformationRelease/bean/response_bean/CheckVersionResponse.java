package com.jld.InformationRelease.bean.response_bean;

/**
 * Created by boping on 2017/8/21.
 */

public class CheckVersionResponse {

    private String version;
    private String url;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CheckVersionResponse{" +
                "version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
