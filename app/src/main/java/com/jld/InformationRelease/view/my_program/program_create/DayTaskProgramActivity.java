package com.jld.InformationRelease.view.my_program.program_create;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_program.adapter.DayTaskAdapter;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.app.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

public class DayTaskProgramActivity extends BaseProgramCompileActivity implements View.OnClickListener {

    private static final java.lang.String TAG = "DayTaskProgramActivity";
    private DayTaskAdapter mAdapter;
    private static final int ADD_IMAGE = 0x10;
    private ImageView mAdd_task;
    private String[] mNames;
    private String[] mTableIds;
    private ArrayList<DayTaskItem> mDayTaskItems;
    private ArrayList<DayTaskItem> mBeforeDayTaskItems = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_task_program);
        mDayTaskItems = mProgramBean.getDayProgram();
        mBeforeDayTaskItems.addAll(mDayTaskItems);
        mNames = new String[mProgramDatas.size()];
        mTableIds = new String[mProgramDatas.size()];
        for (int i = 0; i < mProgramDatas.size(); i++) {
            String name = mProgramDatas.get(i).getTab();
            int id = mProgramDatas.get(i).getTable_id();
            mNames[i] = name;
            mTableIds[i] = id + "";
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
        mIb_tool = (ImageButton) findViewById(R.id.titlebar_tool);
        mIb_tool.setVisibility(View.VISIBLE);
        mIb_tool.setOnClickListener(this);
        TextView title_center = (TextView) findViewById(R.id.title_center);
        title_center.setText(R.string.day_task);
        findViewById(R.id.title_right).setVisibility(View.GONE);
        //recyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_day_task);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DayTaskProgramActivity.this));
        mAdapter = new DayTaskAdapter(mDayTaskItems, DayTaskProgramActivity.this);
        if (mDayTaskItems.size() == 0)
            mAdapter.addItem(new DayTaskItem());
        mAdapter.setOnItemClickListen(mOnItemClickListen);
        mRecyclerView.setAdapter(mAdapter);
    }

    DayTaskAdapter.OnItemClickListen mOnItemClickListen = new DayTaskAdapter.OnItemClickListen() {
        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
        Dialog.Builder builder = null;

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
                    LogUtil.d(TAG, "index:" + index);//节目名称设置
                    mAdapter.datas.get(position).setProgramName(mNames[index]);
                    mAdapter.datas.get(position).setProgramLocalId(mTableIds[index]);
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

        @Override
        public void onStartClickListen(View view, final int position) {
            builder = new TimePickerDialog.Builder(isLightTheme ? R.style.Material_App_Dialog_TimePicker_Light : R.style.Material_App_Dialog_TimePicker, 24, 00) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {//设置节目启动时间
                    TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
//                    Toast.makeText(DayTaskProgramActivity.this, "Time is " + dialog.getFormattedTime(SimpleDateFormat.getTimeInstance()), Toast.LENGTH_SHORT).show();
                    mAdapter.datas.get(position).setStateTime(dialog.getFormattedTime(SimpleDateFormat.getTimeInstance()));
                    mAdapter.notifyDataSetChanged();
                    super.onPositiveActionClicked(fragment);
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

        @Override
        public void onStopClickListen(View view, final int position) {
            builder = new TimePickerDialog.Builder(isLightTheme ? R.style.Material_App_Dialog_TimePicker_Light : R.style.Material_App_Dialog_TimePicker, 24, 00) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {//设置节目停止时间
                    TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                    mAdapter.datas.get(position).setStopTime(dialog.getFormattedTime(SimpleDateFormat.getTimeInstance()));
                    mAdapter.notifyDataSetChanged();
                    super.onPositiveActionClicked(fragment);
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
                mHandler.sendEmptyMessageDelayed(ADD_IMAGE, 200);
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.titlebar_tool:
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

    @Override
    public void saveProgram() {
        String userID = mSp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            return;
        }
        mProgramBean.setTime(TimeUtil.getTodayDateTime());
        mProgramBean.setDayProgram(mDayTaskItems);//需要推送终端的Mac地址
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state("0");
        LogUtil.d(TAG, "mProgramBean:" + mProgramBean);
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
        mProgramBean.setTime(TimeUtil.getTodayDateTime());
        mProgramBean.setDayProgram(mDayTaskItems);
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state("0");
        Intent intent = new Intent();
        intent.putExtra("body", mProgramBean);
        LogUtil.d(TAG, "programPush:" + mProgramBean);
        setResult(mProgramResultCode, intent);//编辑结果返回
        finish();
    }

    @Override
    public boolean isDataChange() {
        if (mIsAgainCompile) {
            if (mDayTaskItems.size() != mBeforeDayTaskItems.size()) {
                return true;
            } else {
                for (int i = 0; i < mDayTaskItems.size(); i++) {
                    if (mDayTaskItems.get(i) != mBeforeDayTaskItems.get(i))
                        return true;
                }
            }
        } else if (mDayTaskItems.size() > 0) {
            return true;
        }
        return false;
    }
}
