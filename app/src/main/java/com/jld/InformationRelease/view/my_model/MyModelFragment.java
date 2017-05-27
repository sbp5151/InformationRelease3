package com.jld.InformationRelease.view.my_model;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseRecyclerViewAdapterClick;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.login_register.LoginActivity;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * 我的模板
 */
public class MyModelFragment extends Fragment {

    private static final java.lang.String TAG = "MyModelFragment";
    private MainActivity mActivity;
    private ArrayList<ProgramBean> mAllProgram;
    private MyModelRecyclerAdapter mAdapter;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.d(TAG, "hidden:" + hidden);
        initData();
        if (mAllProgram != null)
            mAdapter.update(mAllProgram);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_model, container, false);
        initData();
        if (mAllProgram != null)
            initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        if (mAllProgram != null)
            mAdapter.update(mAllProgram);
    }

    private void initView(View view) {
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.terminal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyModelRecyclerAdapter(mActivity, mAllProgram);
        mAdapter.setMyItemSelectClick(mMyItemClick);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initData() {
        SharedPreferences sp = mActivity.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
        String userid = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userid)) {
            ToastUtil.showToast(mActivity, getResources().getString(R.string.please_login), 3000);
            mActivity.toActivity(LoginActivity.class);
        }
        ProgramDao programDao = ProgramDao.getInstance(mActivity, userid);
        try {
            mAllProgram = programDao.getAllProgram();
            LogUtil.d(TAG, "mAllProgram:" + mAllProgram);
        } catch (JSONException e) {
            ToastUtil.showToast(mActivity, getResources().getString(R.string.read_data_error), 3000);
            e.printStackTrace();
        }
    }

    BaseRecyclerViewAdapterClick.MyItemClick mMyItemClick = new BaseRecyclerViewAdapterClick.MyItemClick() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(mActivity, MyModelContentActivity.class);
            intent.putExtra("data", mAllProgram.get(position));
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
}
