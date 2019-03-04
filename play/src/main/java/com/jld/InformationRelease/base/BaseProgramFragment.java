package com.jld.InformationRelease.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.jld.InformationRelease.bean.response.ProgramResponseBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseProgramFragment extends Fragment {


    protected FragmentActivity mContext;
    protected ProgramResponseBean mData;

    //私有
    protected BaseProgramFragment() {
    }

    public static BaseProgramFragment getInstance(ProgramResponseBean data) {
        //数据传递
        BaseProgramFragment fragment1 = new BaseProgramFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        fragment1.setArguments(bundle);
        return fragment1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            mData = (ProgramResponseBean) bundle.getSerializable("data");
        } else {
            try {
                throw new Exception("ProgramFragment需要传入参数");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mContext = getActivity();
    }
}
