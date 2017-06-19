package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.model.FileModel;

import java.util.ArrayList;

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

    public void updateFile(String imgPath, int requesTag) {

        mFileModel.updateFile(imgPath, this, requesTag);
    }

    public void updateFiles(ArrayList<String> imgPath, int requesTag) {

        mFileModel.updateFiles(imgPath, this, requesTag);
    }
}
