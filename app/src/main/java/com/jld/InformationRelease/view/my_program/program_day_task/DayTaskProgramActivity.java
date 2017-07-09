package com.jld.InformationRelease.view.my_program.program_day_task;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.DayTaskBean;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.db.ProgramDao2;
import com.jld.InformationRelease.dialog.SetProgramTabDialog;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.my_program.adapter.DayTaskAdapter;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.app.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DayTaskProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<DayTaskBean.DayTaskItem> datas;
    private DayTaskAdapter mAdapter;
    private static final int ADD_IMAGE = 0x10;
    private static final java.lang.String TAG = "DayTaskProgramActivity";

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_IMAGE:
                    mAdapter.addItem(new DayTaskBean.DayTaskItem());
                    break;
            }
        }
    };
    private ImageView mAdd_task;
    private String[] mNames;
    private String[] mTableIds;
    private ImageButton mIb_tool;
    private ArrayList<ProgramBean> mProgram_data;
    private DayTaskBean mDayTaskBean;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_task_program);
        mProgram_data = getIntent().getParcelableArrayListExtra("program_data");
        mDayTaskBean = getIntent().getParcelableExtra("task_data");
        LogUtil.d(TAG, "mProgram_data:" + mProgram_data + "\n" + "mDayTaskBean:" + mDayTaskBean);
        if (mDayTaskBean == null) {
            mDayTaskBean = new DayTaskBean();
            SetProgramTabDialog tabDialog = new SetProgramTabDialog(this, new SetProgramTabDialog.OnProgramTabListen() {
                @Override
                public void onSetTab(String tab) {
                    mDayTaskBean.setTab(tab);
                }
            });
            tabDialog.show(getFragmentManager(), "");
        }
        mDayTaskBean.setType("2");//每日任务类型
        datas = mDayTaskBean.getProgram_item();
        mNames = new String[mProgram_data.size()];
        mTableIds = new String[mProgram_data.size()];
        mUserId = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE).getString(UserConstant.USER_ID, "");
        for (int i = 0; i < mProgram_data.size(); i++) {
            String name = mProgram_data.get(i).getTab();
            int id = mProgram_data.get(i).getTable_id();
            mNames[i] = name;
            mTableIds[i] = id + "";
        }
        initView();

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
        mAdapter = new DayTaskAdapter(datas, DayTaskProgramActivity.this);
        if (datas.size() == 0)
            mAdapter.addItem(new DayTaskBean.DayTaskItem());
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

    /**
     * 推送、预览、保存
     */
    public void showPopupwindow() {
        final PopupWindow mPopupWindow = new PopupWindow(this);
        View contentView = getLayoutInflater().inflate(R.layout.program_popupwindow_layout, null);
        contentView.findViewById(R.id.pp_program_push).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();

            }
        });
        contentView.findViewById(R.id.pp_preview).setVisibility(View.GONE);
//                .setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mPopupWindow.dismiss();
//
//            }
//        });
        contentView.findViewById(R.id.pp_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                programSave();
                finish();
            }
        });
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(GeneralUtil.dip2px(this, 100));
        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000000));//透明背景
        mPopupWindow.setAnimationStyle(R.style.push_popupwindow_style);//动画
        mPopupWindow.showAsDropDown(mIb_tool, GeneralUtil.dip2px(this, 21), GeneralUtil.dip2px(this, -21));
    }

    private void programSave() {
        if (TextUtils.isEmpty(mUserId)) {
            ToastUtil.showToast(this, getString(R.string.please_login), 3000);
            return;
        }
        LogUtil.d(TAG, "datas:" + datas);
        mDayTaskBean.setModel_img("");
        mDayTaskBean.setProgram_item(datas);
//        mDayTaskBean.setIsLoadSucceed("0");
        mDayTaskBean.setCreation_time(TimeUtil.getTodayDateTime());
        mDayTaskBean.setUpload_state("0");
        mDayTaskBean.setUserid(mUserId);
        ProgramDao2 dao = ProgramDao2.getInstance(this);
        dao.addData(mDayTaskBean);
    }
}
