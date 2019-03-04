package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.interfaces.IBaseListener;
import com.jld.InformationRelease.interfaces.IBaseView;
import com.jld.InformationRelease.model.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/25 15:11
 */
public class FilePresenter extends BasePresenterImpl<FileResponseBean> {

    Context mContext;
    private final FileModel mFileModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public FilePresenter(Context context, IViewToPresenter view) {
        super(view);
        mContext = context;
        mFileModel = new FileModel(context);
    }

    public void uploadFile(String imgPath, int requesTag) {

        mFileModel.uploadFile(imgPath, this, requesTag);
    }

    public void uploadFiles(ArrayList<String> imgPath, int requesTag) {
        mFileModel.uploadFiles(imgPath, this, requesTag);
    }

    public void downloadVideo(List<String> voids,final IBaseView listener){
        mFileModel.downloadVideo(voids, new IBaseListener() {
            @Override
            public void succeed() {
                listener.succeed();
            }

            @Override
            public void failed(String msg) {
                listener.failed(msg);
            }
        });
    }
}
