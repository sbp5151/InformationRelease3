package com.jld.InformationRelease.view.my_terminal;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.DayTaskBean;
import com.jld.InformationRelease.bean.request_bean.UnbindRequest;
import com.jld.InformationRelease.bean.request_bean.UpdateTerminalRequest;
import com.jld.InformationRelease.bean.response_bean.GetTerminalResponse;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.db.ProgramDao2;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.TerminalFunctionPresenter;
import com.jld.InformationRelease.presenter.UpdateTerminalPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.my_terminal.adapter.TerminalAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 一：我的设备
 */
public class MyTerminalFragment extends Fragment implements
        TerminalAdapter.OnRecyclerViewItemClickListener, IViewListen<Object> {

    private RecyclerView mRecyclerView;
    private MainActivity mActivity;
    private ArrayList<TerminalBeanSimple> terminals = new ArrayList<>();
    public TerminalAdapter mAdapter;
    private ProgressDialog mDialog;
    private ImageButton mPush;
    private TextView mTvComplete;
    private TextView mTitle_tx;
    private PopupWindow mPopupWindow;
    public static final int mProgramRequestCode = 0x21;//节目编辑数据返回
    public static final int mProgramResultCode = 0x22;//节目编辑数据返回
    public static final int UNBIND_REQUEST_TAG = 0x23;//解绑数据返回
    private static final int UPDATE_TERMINAL = 0x24;//获取设备数据
    private String mUserId;
    public static final String TAG = "MyTerminalFragment";
    private static final int INIT_DATA = 0x01;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INIT_DATA:
                    initData();
                    break;
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.d(TAG, "hidden:" + hidden);
        if (!hidden)
            mHandler.sendEmptyMessageDelayed(INIT_DATA, 200);//延迟加载 提升用户体验
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_terminal, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        //title
        View title_view = view.findViewById(R.id.my_terminal_title);
        ImageButton menu = (ImageButton) title_view.findViewById(R.id.toolbar_left);
        mTvComplete = (TextView) title_view.findViewById(R.id.toolbar_complete);
        mPush = (ImageButton) title_view.findViewById(R.id.toolbar_push);
        menu.setOnClickListener(mOnClickListener);
        mTitle_tx = (TextView) title_view.findViewById(R.id.toolbar_title);
        mTitle_tx.setText(getString(R.string.device_list));
        mPush.setOnClickListener(mOnClickListener);
        mTvComplete.setOnClickListener(mOnClickListener);
        //recycler
        mRecyclerView = (RecyclerView) view.findViewById(R.id.terminal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TerminalAdapter(terminals, mActivity);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

    }


    public void initData() {
        //加载所有绑定的终端设备
        UpdateTerminalPresenter presenter = new UpdateTerminalPresenter(this, mActivity);
        UpdateTerminalRequest requestBody = new UpdateTerminalRequest();
        SharedPreferences sp = mActivity.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
        mUserId = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(mUserId)) {
            ToastUtil.showToast(mActivity, getString(R.string.please_login), 3000);
            return;
        }
        requestBody.setUserId(mUserId);
        requestBody.setSign(MD5Util.getMD5(Constant.S_KEY + mUserId));
        presenter.updateTerminal(requestBody, UPDATE_TERMINAL);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toolbar_left:
                    if (mActivity.mDrawer.isDrawerOpen(mActivity.mNavigationView)) {
                        mActivity.mDrawer.closeDrawer(mActivity.mNavigationView);
                    } else
                        mActivity.mDrawer.openDrawer(mActivity.mNavigationView);
                    break;
                case R.id.toolbar_complete://切换编辑状态
                    changeCompleteState();
                    break;
                case R.id.toolbar_push://对终端功能操作
                    showPopupwindow();
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showPopupwindow() {
        mPopupWindow = new PopupWindow(mActivity);
        View contentView = mActivity.getLayoutInflater().inflate(R.layout.popupwindow_layout, null);
        contentView.findViewById(R.id.pp_program_unbind).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_showdown).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_restart).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_time_showdown).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_volume_adjust).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_get_screen).setOnClickListener(ppOnClickListener);

        mPopupWindow.setContentView(contentView);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(GeneralUtil.dip2px(mActivity, 100));
        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000000));//透明背景
        mPopupWindow.setAnimationStyle(R.style.push_popupwindow_style);//动画
        mPopupWindow.showAsDropDown(mPush, GeneralUtil.dip2px(mActivity, 21), GeneralUtil.dip2px(mActivity, -21));
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mPush.setClickable(true);
//                mPush.setEnabled(true);
//            }
//        });
    }

    /**
     * 推送、预览、保存
     */
//    public void showPopupwindow() {
//        final PopupWindow mPopupWindow = new PopupWindow(mActivity);
//        View contentView = mActivity.getLayoutInflater().inflate(R.layout.popupwindow_layout, null);
////        contentView.findViewById(R.id.pp_program_push).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mPopupWindow.dismiss();
////
////            }
////        });
////        contentView.findViewById(R.id.pp_preview).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mPopupWindow.dismiss();
////
////            }
////        });
////        contentView.findViewById(R.id.pp_save).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mPopupWindow.dismiss();
////            }
////        });
//        mPopupWindow.setContentView(contentView);
//        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        mPopupWindow.setWidth(GeneralUtil.dip2px(mActivity, 100));
//        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000000));//透明背景
//        mPopupWindow.setAnimationStyle(R.style.push_popupwindow_style);//动画
//        mPopupWindow.showAsDropDown(mPush, GeneralUtil.dip2px(mActivity, 21), GeneralUtil.dip2px(mActivity, -21));
//    }
    View.OnClickListener ppOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<String> pushId = getCheckMac();
            LogUtil.d(TAG, "ids:" + pushId);
            if (pushId.size() == 0) {
                ToastUtil.showToast(mActivity, getString(R.string.please_select_terminal), 3000);
                return;
            }
            switch (view.getId()) {
                case R.id.pp_program_unbind://节目推送
                    mPopupWindow.dismiss();
                    // TODO: 2017/6/17 解绑
                    UnbindRequest body = new UnbindRequest();
                    body.setDeviceMacs(pushId);
                    body.setUserId(mUserId);
                    body.setSign(MD5Util.getMD5(Constant.S_KEY + mUserId));
                    TerminalFunctionPresenter presenter = new TerminalFunctionPresenter(mActivity, MyTerminalFragment.this);
                    presenter.unbind(body, UNBIND_REQUEST_TAG);
                    break;
                case R.id.pp_showdown://关机
                    ToastUtil.showToast(mActivity, getString(R.string.function_developed), 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_restart://重启
                    ToastUtil.showToast(mActivity, getString(R.string.function_developed), 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_time_showdown://定时开关机
                    ToastUtil.showToast(mActivity, getString(R.string.function_developed), 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_volume_adjust://音量调节
                    ToastUtil.showToast(mActivity, getString(R.string.function_developed), 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_get_screen://获取截屏
                    ToastUtil.showToast(mActivity, getString(R.string.function_developed), 3000);
                    mPopupWindow.dismiss();
                    break;
            }
            changeCompleteState();
        }
    };

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (mProgramResultCode == resultCode && requestCode == mProgramRequestCode && data != null) {
//            ProgramBean mProgram = (ProgramBean) data.getSerializableExtra("body");
//            LogUtil.d(TAG, "program:" + mProgram);
//            //开启service上传节目
//            mActivity.mProgram = mProgram;
//            mActivity.createProgramService();
//        }
//    }


    public void changeCompleteState() {
        if (isCheck()) {//取消
            setCheck(false);
            mPush.setVisibility(View.GONE);
            mTvComplete.setText(getResources().getString(R.string.compile));
            mTitle_tx.setText(getString(R.string.device_list));
        } else {//编辑状态
            setCheck(true);
            mTitle_tx.setText(getString(R.string.select_device_complete));
            mPush.setVisibility(View.VISIBLE);
            mTvComplete.setText(getResources().getString(R.string.cancle));
        }
    }

    @Override
    public void onItemClick(View view, int position) {
//        ToastUtil.showToast(mActivity, position + "", 3000);
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

    /**
     * 获取被选中的设备终端Mac地址
     *
     * @return
     */
    public ArrayList<String> getCheckMac() {
        List<TerminalBeanSimple> data = mAdapter.getData();
        ArrayList<String> checkMac = new ArrayList<>();
        for (TerminalBeanSimple bean : data) {
            if (bean.getCheck())
                checkMac.add(bean.getMac());
        }
        return checkMac;
    }

    @Override
    public void loadDataSuccess(Object data, int requestTag) {
        LogUtil.d(TAG, "GetTerminalResponse:" + data);
        if (UNBIND_REQUEST_TAG == requestTag) {
            initData();
        } else if (requestTag == UPDATE_TERMINAL) {
            GetTerminalResponse response = (GetTerminalResponse) data;
            //获取数据成功，更新界面
            ArrayList<TerminalBeanSimple> items = response.getItems();

            SharedPreferences.Editor edit = mActivity.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE).edit();
            edit.putString(Constant.MY_TERMINAL, new Gson().toJson(items));
            edit.apply();
            mAdapter.setDataChange(items);
        }
    }

    private boolean isFirst = true;

    @Override
    public void showProgress(int requestTag) {
        if (requestTag == UPDATE_TERMINAL && isFirst) {//第一次进入显示dialog
            isFirst = false;
            mDialog = new ProgressDialog(mActivity);
            mDialog.setMessage(getString(R.string.loading));
            mDialog.show();
        }
    }

    @Override
    public void hideProgress(int requestTag) {
        if (requestTag == UPDATE_TERMINAL && mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        ToastUtil.showToast(mActivity, e.getMessage().toString(), 3000);
        hideProgress(requestTag);
    }
}
