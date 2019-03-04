package com.jld.InformationRelease.view.my_program.program_create;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_program.program_create.adapter.DayTaskAdapter;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.app.TimePickerDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_terminal.MyDeviceFragment.PROGRAM_RESULT_CODE;

public class DayTaskProgramActivity extends BaseProgramCompileActivity implements View.OnClickListener {

    private static final java.lang.String TAG = "DayTaskProgramActivity";
    private DayTaskAdapter mAdapter;
    private static final int ADD_IMAGE = 0x10;
    private ImageView mAdd_task;
    private String[] mNames;
    private String[] mProgramIds;
    private String[] mTabIds;
    private ArrayList<DayTaskItem> mDayTaskItems;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_IMAGE:
                    mAdapter.addItem(new DayTaskItem());
                    break;
            }
        }
    };
    private String mLastString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_day_task_program);
        mDayTaskItems = mProgramBean.getDayProgram();
        mLastString = mDayTaskItems.toString();
        Log.d(TAG, "mLastString:" + mLastString);
        mNames = new String[mProgramDatas.size()];
        mProgramIds = new String[mProgramDatas.size()];
        mTabIds = new String[mProgramDatas.size()];
        for (int i = 0; i < mProgramDatas.size(); i++) {
            String name = mProgramDatas.get(i).getTab();
            String id = mProgramDatas.get(i).getProgramId();
            int tab_id = mProgramDatas.get(i).getTable_id();
            mNames[i] = name;
            mProgramIds[i] = id;
            mTabIds[i] = tab_id + "";
        }
        initView();
        setPopupWindowListener(mPopupWindowListener);
    }

    private void initView() {
        mAdd_task = (ImageView) findViewById(R.id.iv_add_day_task);
        mAdd_task.setOnClickListener(this);
        //title
        View title = findViewById(R.id.day_task_title);
        title.findViewById(R.id.title_back).setOnClickListener(this);
        mIb_tool = (ImageButton) findViewById(R.id.title_tool);
        mIb_tool.setVisibility(View.VISIBLE);
        mIb_tool.setOnClickListener(this);
        TextView title_center = (TextView) findViewById(R.id.title_center);
        title_center.setText(R.string.day_task);
        findViewById(R.id.title_right).setVisibility(View.GONE);
        //recyclerView
        SwipeMenuRecyclerView recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.rv_day_task);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(DayTaskProgramActivity.this));
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int position = menuBridge.getAdapterPosition();
                mAdapter.removeItem(position);
            }
        });
        mAdapter = new DayTaskAdapter(mDayTaskItems, DayTaskProgramActivity.this);
        if (mDayTaskItems.size() == 0)
            mAdapter.addItem(new DayTaskItem());
        mAdapter.setOnItemClickListen(mOnItemClickListen);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 侧滑菜单 item创建
     */
    SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = getResources().getDimensionPixelSize(R.dimen.swipe_menu_item_width);

            SwipeMenuItem menuItem = new SwipeMenuItem(DayTaskProgramActivity.this)
                    .setBackground(R.drawable.swipe_menu_delete)
                    .setHeight(height)
                    .setWidth(width)
                    .setText(getString(R.string.delete))
                    .setTextColor(getResources().getColor(R.color.white));
            swipeRightMenu.addMenuItem(menuItem);
        }
    };

    DayTaskAdapter.OnItemClickListen mOnItemClickListen = new DayTaskAdapter.OnItemClickListen() {
        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
        Dialog.Builder builder = null;

        //节目名称设置
        @Override
        public void onNameClickListen(View view, final int position) {
            if (mNames.length == 0) {
                ToastUtil.showToast(DayTaskProgramActivity.this, getString(R.string.program_list_null), 3000);
                return;
            }
            builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    super.onPositiveActionClicked(fragment);
                    int index = getSelectedIndex();
                    Log.d(TAG, "index:" + index);//节目名称设置
                    mAdapter.datas.get(position).setProgramName(mNames[index]);
                    mAdapter.datas.get(position).setProgramLocalId(mProgramIds[index]);
                    mAdapter.datas.get(position).setProgramTabId(mTabIds[index]);
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };
            ((SimpleDialog.Builder) builder).items(mNames, 0)
                    .title(getResources().getString(R.string.select_program))
                    .positiveAction(getResources().getString(R.string.sure))
                    .negativeAction(getResources().getString(R.string.cancle));
            DialogFragment fragment = DialogFragment.newInstance(builder);
            FragmentManager fragmentManager = DayTaskProgramActivity.this.getSupportFragmentManager();
            fragment.show(fragmentManager, null);
        }

        //起始时间设置
        @Override
        public void onStartClickListen(View view, final int position) {
            builder = new TimePickerDialog.Builder(isLightTheme ? R.style.Material_App_Dialog_TimePicker_Light : R.style.Material_App_Dialog_TimePicker, 24, 00) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {//设置节目启动时间
                    TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                    DateFormat timeInstance = SimpleDateFormat.getTimeInstance();
                    String startTime = dialog.getFormattedTime(timeInstance);
                    String stopTime = mAdapter.datas.get(position).getStopTime();
                    super.onPositiveActionClicked(fragment);

                    //节目时间重叠检测
                    for (int i = 0; i < mAdapter.datas.size(); i++) {
                        DayTaskItem item = mAdapter.datas.get(i);
                        if (position != i && !TextUtils.isEmpty(item.getStateTime()) && !TextUtils.isEmpty(item.getStopTime())) {
                            Log.d(TAG, "item.getStateTime():" + item.getStateTime());
                            Log.d(TAG, "startTime:" + startTime);
                            Log.d(TAG, "--------");
                            if (startTime.equals(item.getStateTime()) || (TimeUtil.timeCompare(startTime, item.getStateTime()) && TimeUtil.timeCompare(item.getStopTime(), startTime))) {
                                ToastUtil.showToast(DayTaskProgramActivity.this, getString(R.string.play_time_error2), 3000);
                                return;
                            }
                        }
                    }

                    //结束时间大于播放时间检测
                    if (TextUtils.isEmpty(stopTime) || TimeUtil.timeCompare(stopTime, startTime)) {
                        mAdapter.datas.get(position).setStateTime(startTime);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToast(DayTaskProgramActivity.this, getString(R.string.play_time_error), 3000);
                    }
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    Toast.makeText(DayTaskProgramActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    super.onNegativeActionClicked(fragment);
                }
            };
            builder.positiveAction(getResources().getString(R.string.sure))
                    .negativeAction(getResources().getString(R.string.cancel));
            DialogFragment fragment = DialogFragment.newInstance(builder);
            FragmentManager fragmentManager = DayTaskProgramActivity.this.getSupportFragmentManager();
            fragment.show(fragmentManager, null);
        }

        //结束时间设置
        @Override
        public void onStopClickListen(View view, final int position) {
            builder = new TimePickerDialog.Builder(isLightTheme ? R.style.Material_App_Dialog_TimePicker_Light
                    : R.style.Material_App_Dialog_TimePicker, 24, 00) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {//设置节目停止时间
                    TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                    DateFormat timeInstance = SimpleDateFormat.getTimeInstance();
                    Log.d(TAG, "timeInstance:" + timeInstance);
                    String stopTime = dialog.getFormattedTime(timeInstance);
                    String startTime = mAdapter.datas.get(position).getStateTime();
                    super.onPositiveActionClicked(fragment);

                    //节目时间重叠检测
                    for (int i = 0; i < mAdapter.datas.size(); i++) {
                        DayTaskItem item = mAdapter.datas.get(i);
                        if (i != position && !TextUtils.isEmpty(item.getStateTime()) && !TextUtils.isEmpty(item.getStopTime())) {
                            Log.d(TAG, "stopTime:" + stopTime);
                            if (!stopTime.equals("00:00:00") && TimeUtil.timeCompare(stopTime, item.getStateTime()) && TimeUtil.timeCompare(item.getStopTime(), stopTime)) {
                                ToastUtil.showToast(DayTaskProgramActivity.this, getString(R.string.play_time_error2), 3000);
                                return;
                            }
                        }
                    }

                    //结束时间大于播放时间检测
                    if (stopTime.equals("00:00:00") || TextUtils.isEmpty(startTime) || TimeUtil.timeCompare(stopTime, startTime)) {
                        if ("00:00:00".equals(stopTime))
                            stopTime = "24:00:00";
                        mAdapter.datas.get(position).setStopTime(stopTime);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToast(DayTaskProgramActivity.this, getString(R.string.play_time_error), 3000);
                    }
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };
            builder.positiveAction(getResources().getString(R.string.sure))
                    .negativeAction(getResources().getString(R.string.cancel));
            DialogFragment fragment = DialogFragment.newInstance(builder);
            FragmentManager fragmentManager = DayTaskProgramActivity.this.getSupportFragmentManager();
            fragment.show(fragmentManager, null);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_day_task:
                AnimationUtil.togetherRun(mAdd_task, 270);
                mHandler.sendEmptyMessageDelayed(ADD_IMAGE, 135);
                break;
            case R.id.title_back:
                if (isDataChange()) {
                    backSaveDialog();
                } else
                    finish();
                break;
            case R.id.title_tool:
                showPopupwindow();
                break;
        }
    }

    PopupWindowListener mPopupWindowListener = new PopupWindowListener() {
        @Override
        public void onPreview() {
        }

        @Override
        public void onProgramPush() {
            if (mDayTaskItems.size() > 0)
                terminalSelect();
            else
                ToastUtil.showToast(DayTaskProgramActivity.this, getResources().getString(R.string.please_compile), 3000);
        }
    };

    /**
     * 节目数据是否填写完整
     *
     * @return
     */
    private boolean programIsFull() {
        for (DayTaskItem item : mDayTaskItems) {
            if (TextUtils.isEmpty(item.getProgramName())
                    || TextUtils.isEmpty(item.getStateTime())
                    || TextUtils.isEmpty(item.getStopTime())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void saveProgram() {
        String userID = mSp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            return;
        }
        mProgramBean.setTime(System.currentTimeMillis()+"");
        mProgramBean.setDayProgram(mDayTaskItems);
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state(Constant.UPLOAD_STATE_NOT);
        Log.d(TAG, "mProgramBean:" + mProgramBean);
        try {
            ProgramDao mProgramDao = ProgramDao.getInstance(this);
            if (mIsAgainCompile) {
                mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
                mProgramDao.updateInDataBaseId(mProgramBean, userID);
            } else
                mProgramDao.addProgram(mProgramBean, userID);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void programPush() {

        if (!programIsFull()) {
            ToastUtil.showToast(this, getString(R.string.please_compile_full), 3000);
            return;
        }
        String userID = mSp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            toActivity(LoginActivity.class);
            return;
        }
        if (mCheckMac.size() <= 0) {
            ToastUtil.showToast(this, getResources().getString(R.string.terminal_id_no_null), Toast.LENGTH_SHORT);
            return;
        }
        mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
        mProgramBean.setDayProgram(mDayTaskItems);
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state(Constant.UPLOAD_STATE_NOT);
        Intent intent = new Intent();
        intent.putExtra("body", mProgramBean);
        Log.d(TAG, "节目编辑数据返回:" + mProgramBean);
        setResult(PROGRAM_RESULT_CODE, intent);//编辑结果返回
        finish();
    }

    @Override
    public boolean isDataChange() {
        String toString = mDayTaskItems.toString();
        Log.d(TAG, "mLastString:" + mLastString);
        Log.d(TAG, "toString:" + toString);
        if (mIsAgainCompile) {
            if (toString.equals(mLastString)) {
                return false;
            } else {
                return true;
            }
        } else if (mDayTaskItems.size() > 0) {
            return true;
        }
        return false;
    }
}
