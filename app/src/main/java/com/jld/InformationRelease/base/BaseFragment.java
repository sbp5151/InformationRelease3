package com.jld.InformationRelease.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jld.InformationRelease.util.LogUtil;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/11 17:23
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.setLogTag(getClass().getName());
    }
}
