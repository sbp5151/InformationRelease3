package com.jld.InformationRelease.view.my_terminal;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.request_bean.UnbindRequest;
import com.jld.InformationRelease.bean.request_bean.UpdateTerminalRequest;
import com.jld.InformationRelease.bean.response_bean.GetTerminalResponse;
import com.jld.InformationRelease.bean.response_bean.DeviceBeanSimple;
import com.jld.InformationRelease.dialog.AlertTextDialog;
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
import com.jld.InformationRelease.view.my_terminal.adapter.DeviceAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 一：我的设备
 */
public class MyDeviceFragment extends Fragment implements
        DeviceAdapter.OnRecyclerViewItemClickListener, IViewListen<Object> {

    private SwipeMenuRecyclerView mRecyclerView;
    private MainActivity mActivity;
    private ArrayList<DeviceBeanSimple> terminals = new ArrayList<>();
    public DeviceAdapter mAdapter;
    private ImageButton mPush;
    private TextView mTvComplete;
    private TextView mTitle_tx;
    private PopupWindow mPopupWindow;
    public static final int mProgramRequestCode = 0x21;//节目编辑数据返回
    public static final int mProgramResultCode = 0x22;//节目编辑数据返回
    public static final int UNBIND_REQUEST_TAG = 0x23;//解绑数据返回
    private static final int UPDATE_TERMINAL = 0x24;//获取设备数据
    private String mUserId;
    public static final String TAG = "MyDeviceFragment";
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
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        mTvComplete.setVisibility(View.GONE);
        mPush = (ImageButton) title_view.findViewById(R.id.toolbar_push);
        menu.setOnClickListener(mOnClickListener);
        mTitle_tx = (TextView) title_view.findViewById(R.id.toolbar_title);
        mTitle_tx.setText(getString(R.string.device_list));
        mPush.setOnClickListener(mOnClickListener);
        mTvComplete.setOnClickListener(mOnClickListener);
        //recycler
        mRecyclerView = (SwipeMenuRecyclerView) view.findViewById(R.id.terminal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mSwipeMenuItemClickListener);
        mAdapter = new DeviceAdapter(terminals, mActivity);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        //
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.terminal_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.d(TAG,"onRefresh");
                initData();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
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
    /**
     * 侧滑菜单 item创建
     */
    SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = getResources().getDimensionPixelSize(R.dimen.swipe_menu_item_width);
            //解绑 menu
            SwipeMenuItem menuItemPush = new SwipeMenuItem(mActivity)
                    .setBackground(R.drawable.swipe_menu_delete)
                    .setHeight(height)
                    .setWidth(width)
                    .setText(getString(R.string.unbind))
                    .setTextColor(getResources().getColor(R.color.white));
            swipeRightMenu.addMenuItem(menuItemPush);
        }
    };
    /**
     * 侧滑菜单 item点击监听
     */
    SwipeMenuItemClickListener mSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            final int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {//右侧菜单
                AlertTextDialog unbindDialog = new AlertTextDialog(mActivity, getString(R.string.unbind_dev_affirm), new AlertTextDialog.OnAlertTextListen() {
                    @Override
                    public void onConfirm() {
                        unbindTerminal(adapterPosition);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                unbindDialog.show(getFragmentManager(),"dialog");
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {//左侧菜单
            }
        }
    };

    private void unbindTerminal(int position){
        DeviceBeanSimple terminal = terminals.get(position);
        ArrayList<String> macs = new ArrayList<>();//解绑
        macs.add(terminal.getMac());
        UnbindRequest body = new UnbindRequest();
        body.setDeviceMacs(macs);
        body.setUserId(mUserId);
        body.setSign(MD5Util.getMD5(Constant.S_KEY + mUserId));
        TerminalFunctionPresenter presenter = new TerminalFunctionPresenter(mActivity, MyDeviceFragment.this);
        presenter.unbind(body, UNBIND_REQUEST_TAG);
    }
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
                    UnbindRequest body = new UnbindRequest();
                    body.setDeviceMacs(pushId);
                    body.setUserId(mUserId);
                    body.setSign(MD5Util.getMD5(Constant.S_KEY + mUserId));
                    TerminalFunctionPresenter presenter = new TerminalFunctionPresenter(mActivity, MyDeviceFragment.this);
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
        List<DeviceBeanSimple> data = mAdapter.getData();
        ArrayList<String> checkMac = new ArrayList<>();
        for (DeviceBeanSimple bean : data) {
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
            ArrayList<DeviceBeanSimple> items = response.getItems();

            SharedPreferences.Editor edit = mActivity.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE).edit();
            edit.putString(Constant.MY_TERMINAL, new Gson().toJson(items));
            edit.apply();
            terminals.clear();
            terminals.addAll(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean isFirst = true;

    @Override
    public void showProgress(int requestTag) {
        if (requestTag == UPDATE_TERMINAL && isFirst) {//第一次进入显示dialog
            isFirst = false;
        }
    }

    @Override
    public void hideProgress(int requestTag) {
        if (requestTag == UPDATE_TERMINAL) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        ToastUtil.showToast(mActivity, e.getMessage().toString(), 3000);
        hideProgress(requestTag);
    }
}
