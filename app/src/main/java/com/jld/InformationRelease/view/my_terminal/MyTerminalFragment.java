package com.jld.InformationRelease.view.my_terminal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.request_bean.GetTerminalRequest;
import com.jld.InformationRelease.bean.response_bean.GetTerminalResponse;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.presenter.GetTerminalPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;

import java.util.ArrayList;
import java.util.List;

public class MyTerminalFragment extends Fragment implements TerminalAdapter.OnRecyclerViewItemClickListener, IViewToPresenter<GetTerminalResponse> {

    private RecyclerView mRecyclerView;
    private FragmentActivity mActivity;
    private ArrayList<TerminalBeanSimple> terminals = new ArrayList<>();
    private TerminalAdapter mAdapter;
    private static final int GET_TERMINAL = 0x21;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_device, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.terminal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TerminalAdapter(terminals, mActivity);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        initData();
        return view;
    }

    private void initData() {
        //加载所有绑定的终端设备
        GetTerminalPresenter presenter = new GetTerminalPresenter(this, mActivity);
        GetTerminalRequest requestBody = new GetTerminalRequest();
        SharedPreferences sp = mActivity.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
        String userId = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userId)) {
            ToastUtil.showToast(mActivity, getString(R.string.please_login), 3000);
            return;
        }
        requestBody.setUserId(userId);
        requestBody.setSign(MD5Util.getMD5(Constant.S_KEY + userId));
        // TODO: 2017/5/11 请求数据
        //presenter.getTerminal(requestBody,GET_TERMINAL);
        //假数据
        for (int i = 0; i < 20; i++) {
            TerminalBeanSimple terminal = new TerminalBeanSimple("5341514" + i, "1", "", "1", "00:00");
            terminals.add(terminal);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        ToastUtil.showToast(mActivity, position + "", 3000);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public void setCheck(boolean check) {
        if (check != mAdapter.isCompile()) {
            mAdapter.setCompile(check);
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean isCheck() {
        return mAdapter.isCompile();
    }

    public ArrayList<String> getCheckMac() {
        List<TerminalBeanSimple> data = mAdapter.getData();
        ArrayList<String> macs = new ArrayList<>();
        for (TerminalBeanSimple bean : data) {
            if (bean.getCheck())
                macs.add(bean.getDevice_mac());
        }
        return macs;
    }

    @Override
    public void loadDataSuccess(GetTerminalResponse data, int requestTag) {
        //获取数据成功，更新界面
        ArrayList<TerminalBeanSimple> items = data.getItems();
        mAdapter.setDataChange(items);
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {

    }
}
