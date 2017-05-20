package com.jld.InformationRelease.view;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.request_bean.BindingRequest;
import com.jld.InformationRelease.bean.request_bean.ProgramRequestBean;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.presenter.TerminalFunctionPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.util.zxing.activity.CaptureActivity;
import com.jld.InformationRelease.view.my_model.MyModelFragment;
import com.jld.InformationRelease.view.my_terminal.MyTerminalFragment;
import com.jld.InformationRelease.view.my_terminal.ProgramCompileActivity;
import com.jld.InformationRelease.view.my_terminal.SelectModelActivity;
import com.jld.InformationRelease.view.service.ProgramPushService;
import com.jld.InformationRelease.view.settings.SettingFragment;
import com.jld.InformationRelease.view.system_model.SystemModelFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, IViewToPresenter {

    private static final String TAG = "MainActivity";
    private MyTerminalFragment mTerminal_fragment;
    private MyModelFragment mMyModelFragment;
    private SystemModelFragment mSystemModelFragment;
    private Fragment mScanCodeFragment;
    private SettingFragment mSettingFragment;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ImageButton push;
    private TextView mTvComplete;
    private PopupWindow mPopupWindow;
    private TerminalFunctionPresenter mTerminalFunctionPresenter;
    private Dialog mDialog;
    private BindingRequest mBean;
    public static final int REQUEST_TAG_BIND = 0x11;//绑定tag
    public static final int REQUEST_TAG_PUSH = 0x12;//推送tag
    public static final int REQUEST_TAG_SR = 0x13;//关机&重启tag
    public static final int REQUEST_TAG_TS = 0x14;//定时开关机tag
    public static final int REQUEST_TAG_VOLUME = 0x15;//音量调节tag
    public static final int REQUEST_TAG_IMAGE = 0x16;//获取截屏tag
    private int mProgramRequestCode = 0x21;//节目编辑数据返回
    private int mPhotoRequestCode = 0x22;//节目编辑数据返回
    private ProgramRequestBean mProgram;
    private TextView mTitle_tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d(TAG, "onCreate");
        initView();
    }

    public void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        //toolbar
        View content = findViewById(R.id.main_content);
        View view = content.findViewById(R.id.main_toolbar);
        mTvComplete = (TextView) view.findViewById(R.id.toolbar_complete);
        push = (ImageButton) view.findViewById(R.id.toolbar_push);
        ImageButton menu = (ImageButton) view.findViewById(R.id.toolbar_left);
        mTitle_tx = (TextView) findViewById(R.id.toolbar_title);
        mTitle_tx.setText(getString(R.string.device_list));
        menu.setOnClickListener(mOnClickListener);
        push.setOnClickListener(mOnClickListener);
        mTvComplete.setOnClickListener(mOnClickListener);
        //默认值
        mNavigationView.getMenu().getItem(0).setChecked(true);
        switchFragment(R.id.menu_my_terminal);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toolbar_left:
                    if (mDrawer.isDrawerOpen(mNavigationView)) {
                        mDrawer.closeDrawer(mNavigationView);
                    } else
                        mDrawer.openDrawer(mNavigationView);
                    break;
                case R.id.toolbar_complete://切换编辑状态
                    changeCompleteState();
                    break;
                case R.id.toolbar_push://对终端功能操作
                    showPopupwindow();
                    push.setEnabled(false);
                    push.setClickable(false);
                    break;
            }
        }
    };

    public void changeCompleteState() {
        if (mTerminal_fragment != null) {
            MyTerminalFragment terminal_fragment = (MyTerminalFragment) mTerminal_fragment;
            if (terminal_fragment.isCheck()) {//取消
                terminal_fragment.setCheck(false);
                push.setVisibility(View.GONE);
                mTvComplete.setText(getResources().getString(R.string.completer));
                mTitle_tx.setText(getString(R.string.device_list));
            } else {//编辑状态
                mTitle_tx.setText(getString(R.string.select_device_complete));
                terminal_fragment.setCheck(true);
                push.setVisibility(View.VISIBLE);
                mTvComplete.setText(getResources().getString(R.string.cancle));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        int id = item.getItemId();
        switchFragment(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static final String MY_TERMINAL_TAG = "my_terminal_tag";
    public static final String MY_MODEL_TAG = "my_model_tag";
    public static final String SYSTEM_MODEL_TAG = "system_model_tag";
    public static final String SCAN_CODE_TAG = "scan_code_tag";
    public static final String SETTING_TAG = "setting_tag";
    HashMap<Integer, Fragment> maps = new HashMap();

    public void switchFragment(int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (id == R.id.menu_my_terminal) {//我的终端
            mTerminal_fragment = (MyTerminalFragment) fm.findFragmentByTag(MY_TERMINAL_TAG);
            if (mTerminal_fragment == null) {
                mTerminal_fragment = new MyTerminalFragment();
                maps.put(id, mTerminal_fragment);
                ft.add(R.id.main_fragment, mTerminal_fragment, MY_TERMINAL_TAG);
            } else {
                ft.show(mTerminal_fragment);
            }
        } else if (id == R.id.menu_my_model) {//我的模板
            mMyModelFragment = (MyModelFragment) fm.findFragmentByTag(MY_MODEL_TAG);
            if (mMyModelFragment == null) {
                mMyModelFragment = new MyModelFragment();
                maps.put(id, mMyModelFragment);
                ft.add(R.id.main_fragment, mMyModelFragment, MY_MODEL_TAG);
            } else {
                ft.show(mMyModelFragment);
            }
        } else if (id == R.id.menu_system_model) {//系统模板
            mSystemModelFragment = (SystemModelFragment) fm.findFragmentByTag(SYSTEM_MODEL_TAG);
            if (mSystemModelFragment == null) {
                mSystemModelFragment = new SystemModelFragment();
                maps.put(id, mSystemModelFragment);
                ft.add(R.id.main_fragment, mSystemModelFragment, SYSTEM_MODEL_TAG);
            } else {
                ft.show(mSystemModelFragment);
            }
        } else if (id == R.id.menu_scan_code) {//扫描添加
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, mPhotoRequestCode);
        } else if (id == R.id.menu_setting) {//设置
            mSettingFragment = (SettingFragment) fm.findFragmentByTag(SETTING_TAG);
            if (mSettingFragment == null) {
                mSettingFragment = new SettingFragment();
                maps.put(id, mSettingFragment);
                ft.add(R.id.main_fragment, mSettingFragment, SETTING_TAG);
            } else {
                ft.show(mSettingFragment);
            }
        }
        Iterator<Map.Entry<Integer, Fragment>> iterator = maps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Fragment> entry = iterator.next();
            Integer key = entry.getKey();
            if (id == key)
                continue;
            ft.hide(entry.getValue());
        }
        ft.commit();
    }

    /**
     * 结果返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == mPhotoRequestCode && data != null) {//获取图片
            String mac = data.getStringExtra("result");
            boolean isMac = GeneralUtil.isMac(mac);
            if (isMac) {
                showSetNameDialog(mac);
            }
            ToastUtil.showToast(this, isMac + "", 3000);
        } else if (requestCode == mProgramRequestCode && data != null) {//节目制作
            mProgram = (ProgramRequestBean) data.getSerializableExtra("body");
            LogUtil.d(TAG, "program:" + mProgram);
            //开启service上传节目
            createProgramService();
        }
        mNavigationView.getMenu().getItem(0).setChecked(true);
        switchFragment(R.id.menu_my_terminal);
    }

    public void createProgramService() {
        Intent intent = new Intent(this, ProgramPushService.class);
        startService(intent);
        bindService(intent, mCon, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ProgramPushService.MyBinder myBinder = (ProgramPushService.MyBinder) iBinder;
            myBinder.sendPushData(mProgram);
            myBinder.sendCompleteListener(new ProgramPushService.PushCompleteListener() {
                @Override
                public void updateSucceed() {
                    //上传成功 解绑service
                    unbindService(mCon);
                    ToastUtil.showToast(MainActivity.this, getString(R.string.push_succeed), 3000);
                }

                @Override
                public void updateDefeated() {
                    //上传失败，弹框提示是否重新上传
                    showAgainUpdate();
                }
            });
            //开始上传
            myBinder.startPush();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void showAgainUpdate() {
        // TODO: 2017/5/10 创建全局dialog提示是否重新上传
    }

    public void showSetNameDialog(final String mac) {
        View view = LayoutInflater.from(this).inflate(R.layout.set_name_dialog, null);
        final EditText setName = (EditText) view.findViewById(R.id.et_set_name);
        Button confirm = (Button) view.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = setName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (mTerminalFunctionPresenter == null)
                        mTerminalFunctionPresenter = new TerminalFunctionPresenter(MainActivity.this, MainActivity.this);
                    if (mBean == null) {
                        SharedPreferences sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
                        String mobile = sp.getString(UserConstant.USER_ID, "");
                        mBean = new BindingRequest();
                        mBean.setUserId(mobile);
                        mBean.setDevicename(name);
                    }
                    mBean.setDevicemac(mac);
                    String sign = MD5Util.getMD5(Constant.S_KEY + mBean.getUserId() + mac);
                    mBean.setSign(sign);
                    mTerminalFunctionPresenter.binding(mBean, 0x12);
                } else
                    ToastUtil.showToast(MainActivity.this, getResources().getString(R.string.input_name), 3000);
            }
        });
        mDialog = new Dialog(this, R.style.CustomDialog);
//        mDialog.setCanceledOnTouchOutside();
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
        mDialog.setContentView(view);
        mDialog.show();
    }

    public void showPopupwindow() {
        mPopupWindow = new PopupWindow(this);
        View contentView = getLayoutInflater().inflate(R.layout.popupwindow_layout, null);
        contentView.findViewById(R.id.pp_program_push).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_showdown).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_restart).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_time_showdown).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_volume_adjust).setOnClickListener(ppOnClickListener);
        contentView.findViewById(R.id.pp_get_screen).setOnClickListener(ppOnClickListener);

        mPopupWindow.setContentView(contentView);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(GeneralUtil.dip2px(this, 100));
        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
        mPopupWindow.showAsDropDown(push);

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ToastUtil.showToast(MainActivity.this, "消失", 3000);
                push.setClickable(true);
                push.setEnabled(true);
            }
        });
    }

    View.OnClickListener ppOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mTerminalFunctionPresenter == null)
                mTerminalFunctionPresenter = new TerminalFunctionPresenter(MainActivity.this, MainActivity.this);
            ArrayList<String> checkMacs = mTerminal_fragment.getCheckMac();
            if (checkMacs.size() == 0) {
                ToastUtil.showToast(MainActivity.this, getString(R.string.please_select_terminal), 3000);
                return;
            }
            switch (view.getId()) {
                case R.id.pp_program_push://节目推送
                    ToastUtil.showToast(MainActivity.this, "节目推送", 3000);
                    mPopupWindow.dismiss();
                    LogUtil.d(TAG, "mCheckMacs:" + checkMacs);
                    Intent intent = new Intent(MainActivity.this, SelectModelActivity.class);
                    intent.putExtra("checkMacs", checkMacs);
                    // TODO: 2017/5/19 调试
//                    startActivityForResult(intent, mProgramRequestCode);
                    toActivity(ProgramCompileActivity.class);
                    break;
                case R.id.pp_showdown://关机
                    ToastUtil.showToast(MainActivity.this, "关机", 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_restart://重启
                    ToastUtil.showToast(MainActivity.this, "重启", 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_time_showdown://定时开关机
                    ToastUtil.showToast(MainActivity.this, "定时开关机", 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_volume_adjust://音量调节
                    ToastUtil.showToast(MainActivity.this, "音量调节", 3000);
                    mPopupWindow.dismiss();
                    break;
                case R.id.pp_get_screen://获取截屏
                    ToastUtil.showToast(MainActivity.this, "获取截屏", 3000);
                    mPopupWindow.dismiss();
                    break;
            }
            changeCompleteState();
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(Object data, int requestTag) {

    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCon != null) {
            unbindService(mCon);
        }
    }
}
