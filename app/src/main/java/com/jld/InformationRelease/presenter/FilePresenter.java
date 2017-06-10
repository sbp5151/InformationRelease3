package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
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
public class FilePresenter extends BasePresenterImpl<BaseResponse> {

    Context mContext;
    private final FileModel mFileModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public FilePresenter(Context context, IViewListen view) {
        super(view);
        mContext = context;
        mFileModel = new FileModel(context);
    }

    public void updateFile(String body, int requesTag) {

        mFileModel.updateFile(body, this, requesTag);
    }

    public void updateFiles(ArrayList<String> imgPath, int requesTag) {

        for (String path : imgPath)
            mFileModel.updateFile(path, this, requesTag);

    }
}
