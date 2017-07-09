package com.jld.InformationRelease.view.my_program;


import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseProgram;
import com.jld.InformationRelease.bean.DayTaskBean;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.ProgramStateDialogItem;
import com.jld.InformationRelease.bean.response_bean.ProgramLoadStateResponse;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.db.ProgramDao2;
import com.jld.InformationRelease.dialog.ProgramStateProgressDialog;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.ProgramLoadStatePresenter;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.my_program.adapter.MyProgramRecyclerAdapter;
import com.jld.InformationRelease.view.my_program.program_create.ProgramImageActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramTextActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramVideoActivity;
import com.jld.InformationRelease.view.my_program.program_day_task.DayTaskProgramActivity;
import com.jld.InformationRelease.view.service.ProgramPushService;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;
import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramRequestCode;
import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * 节目列表
 */
public class MyProgramFragment extends Fragment implements IViewListen<ProgramLoadStateResponse> {

    private static final java.lang.String TAG = "MyProgramFragment";
    private MainActivity mActivity;
    private ArrayList<ProgramBean> mProgramDatas = new ArrayList<>();
    private ArrayList<DayTaskBean> mDayTaskDatas = new ArrayList<>();
    private ArrayList<BaseProgram> mDatas = new ArrayList<>();
    public MyProgramRecyclerAdapter mAdapter;
    private ImageView mIv_add;
    private static final int NEW_PROGRAM = 0x01;//新建节目
    private static final int DAY_PROGRAM = 0x02;//每日任务
    private static final int INTER_CUT_PROGRAM = 0x03;//插播节目
    private static final int PROGRAM_LOAD_STATE_TAG = 0x21;//节目加载情况

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            super.handleMessage(msg);
            switch (msg.what) {
                case NEW_PROGRAM:
                    intent = new Intent(mActivity, SelectModelActivity.class);
                    intent.putParcelableArrayListExtra("terminal_data", mActivity.mTerminal_fragment.mAdapter.getData());//终端数据
                    startActivityForResult(intent, mProgramRequestCode);
                    break;
                case DAY_PROGRAM:
                    intent = new Intent(mActivity, DayTaskProgramActivity.class);
                    intent.putParcelableArrayListExtra("program_data", mProgramDatas);
                    startActivity(intent);
                    break;
                case INTER_CUT_PROGRAM:
                    mBtn_hide.callOnClick();
                    break;
            }
        }
    };
    private TextView mTvComplete;
    private String mUserid;
    private ProgressBar mProgramBean;
    private ProgramLoadStatePresenter mPresenter;
    private ArrayList<String> loadProgramIds = new ArrayList<>();
    private SharedPreferences mSp;
    ArrayList<TerminalBeanSimple> mTerminals;
    private SwipeRefreshLayout mRefresh;
    private FloatingActionsMenu mBtn_menu;
    private Button mBtn_hide;
    private String[] mNames;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.d(TAG, "hidden:" + hidden);
//        if (!hidden)
//            initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView:");
        View view = inflater.inflate(R.layout.fragment_my_program, container, false);
        mSp = mActivity.getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        mUserid = mSp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(mUserid)) {//需要登录
            ToastUtil.showToast(mActivity, getResources().getString(R.string.please_login), 3000);
            return view;
        }
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG, "onStart:");
        initData();
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop:");
    }

    private void initView(View view) {

        mBtn_hide = (Button) view.findViewById(R.id.btn_hide_inter_cut);
        mBtn_hide.setOnClickListener(mOnClickListener);
        //button
        mBtn_menu = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        view.findViewById(R.id.btn_create_program).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.btn_day_task).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.btn_inter_cut).setOnClickListener(mOnClickListener);
        //title
        View title_view = view.findViewById(R.id.my_progarm_title);
        ImageButton menu = (ImageButton) title_view.findViewById(R.id.toolbar_left);
        mTvComplete = (TextView) title_view.findViewById(R.id.toolbar_complete);
        mProgramBean = (ProgressBar) title_view.findViewById(R.id.pb_program_upload);
        mTvComplete.setOnClickListener(mOnClickListener);
        title_view.findViewById(R.id.toolbar_push).setVisibility(View.GONE);
        menu.setOnClickListener(mOnClickListener);
        TextView title_content = (TextView) title_view.findViewById(R.id.toolbar_title);
        title_content.setText(getString(R.string.program_list));
        //swipeRefresh
        mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_my_program);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        //RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.terminal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyProgramRecyclerAdapter(mActivity, mDatas);
        mAdapter.setMyItemSelectClick(mMyItemClick);
        mRecyclerView.setAdapter(mAdapter);
        //add
        mIv_add = (ImageView) view.findViewById(R.id.imagev_add_trouteam);
        mIv_add.setOnClickListener(mOnClickListener);
    }


    public void initData() {
        ProgramDao programDao = ProgramDao.getInstance(mActivity);
        ProgramDao2 dayTaskDao = ProgramDao2.getInstance(mActivity);
        mDayTaskDatas = dayTaskDao.queryDataAll(mUserid);
        try {
            mProgramDatas = programDao.getAllProgram(mUserid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "mDayTaskDatas:" + mDayTaskDatas);
        LogUtil.d(TAG, "mProgramDatas:" + mProgramDatas);
        mNames = new String[mProgramDatas.size()];
        for (int i = 0; i < mProgramDatas.size(); i++) {
            String name = mProgramDatas.get(i).getTab();
            mNames[i] = name;
        }
        mDatas.clear();
        mDatas.addAll(mProgramDatas);
        mDatas.addAll(mDayTaskDatas);
        LogUtil.d(TAG, "mDatas:" + mDatas);
        if (mDatas.size() >= 2)
            Collections.sort(mDatas, new SortByTime());
        if (mProgramDatas != null || mProgramDatas.size() > 0) {
            mAdapter.update(mDatas);
            loadProgramState();
        } else if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
    }

    /**
     * 按时间降序方式排列节目列表
     */
    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            BaseProgram s1 = (BaseProgram) o1;
            BaseProgram s2 = (BaseProgram) o2;
            if (s1.getType().equals(s2.getType()))
                return TimeUtil.dataOne(s2.getCreation_time()).compareTo(TimeUtil.dataOne(s1.getCreation_time()));
            else {
                if (s1.getType().equals("1"))
                    return -1;
                else return 1;
            }
        }
    }

    /**
     * 设备加载节目情况
     */
    public void loadProgramState() {
        loadProgramIds.clear();
        for (int i = 0; i < mProgramDatas.size(); i++) {
            //和发布时间间隔不超过五分钟
            LogUtil.d(TAG, "timeGap:" +
                    (TimeUtil.toCurrentTimeGap(mProgramDatas.get(i).getCreation_time())));
            if (i == 3 || TimeUtil.toCurrentTimeGap(mProgramDatas.get(i).getCreation_time()) > 1000 * 60 * 5)//只加载前面三个
                break;
            //节目ID不为空并且设备未完全加载完成
            if (!TextUtils.isEmpty(mProgramDatas.get(i).getProgramId()) && mProgramDatas.get(i).getIsLoadSucceed().equals("0")) {
                if (mPresenter == null)
                    mPresenter = new ProgramLoadStatePresenter(this, mActivity);
                mPresenter.programLoadState(mProgramDatas.get(i).getProgramId(), PROGRAM_LOAD_STATE_TAG + i);
                loadProgramIds.add(mProgramDatas.get(i).getProgramId());
            }
        }
        if (loadProgramIds.size() == 0)//没有加载请求 停止refresh
            if (mRefresh.isRefreshing())
                mRefresh.setRefreshing(false);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toolbar_left:
                    if (mActivity.mDrawer.isDrawerOpen(mActivity.mNavigationView)) {
                        mActivity.mDrawer.closeDrawer(mActivity.mNavigationView);
                    } else
                        mActivity.mDrawer.openDrawer(mActivity.mNavigationView);
                    break;
                case R.id.toolbar_complete:
                    if (mAdapter.getCompileState()) {//取消编辑
                        mTvComplete.setText(getResources().getString(R.string.compile));
//                        mIv_add.setVisibility(View.VISIBLE);
                    } else {//编辑
                        mTvComplete.setText(getResources().getString(R.string.complete));
                        mIv_add.setVisibility(View.GONE);
                    }
                    mAdapter.changeCompileState();
                    break;
                case R.id.imagev_add_trouteam://新建节目单
                    AnimationUtil.togetherRun(mIv_add, 270);
                    mHandler.sendEmptyMessageDelayed(NEW_PROGRAM, 270);
                    break;
                case R.id.btn_create_program://创建节目
                    mHandler.sendEmptyMessageDelayed(NEW_PROGRAM, 200);
                    mBtn_menu.collapse();
                    break;
                case R.id.btn_day_task://每日任务
                    mHandler.sendEmptyMessageDelayed(DAY_PROGRAM, 200);
                    mBtn_menu.collapse();
                    break;
                case R.id.btn_inter_cut://插播任务
//                    mHandler.sendEmptyMessageDelayed(INTER_CUT_PROGRAM, 200);
                    mBtn_hide.callOnClick();
                    mBtn_menu.collapse();
                    break;
                case R.id.btn_hide_inter_cut:
                    boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
                    Dialog.Builder builder = null;
                    builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog) {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };
                    ((SimpleDialog.Builder) builder).items(mNames, 0)
                            .title(getResources().getString(R.string.select_program))
                            .positiveAction(getResources().getString(R.string.push))
                            .negativeAction(getResources().getString(R.string.cancle));
                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(mActivity.getSupportFragmentManager(), null);
                    break;
            }
        }
    };

    MyProgramRecyclerAdapter.MyItemClick mMyItemClick = new MyProgramRecyclerAdapter.MyItemClick() {
        @Override
        public void onItemClickListen(View view, int position) {
            if (!mAdapter.getCompileState()) {//在编辑状态不让点击
                BaseProgram baseProgram = mDatas.get(position);
                if (baseProgram.getType().equals("1")) {
                    ProgramBean programBean = (ProgramBean) baseProgram;
                    LogUtil.d(TAG, "programBean:" + baseProgram);
                    Intent intent = null;
                    switch (programBean.getModelId()) {
                        case Constant.VIDEO_MODEL:
                            intent = new Intent(mActivity, ProgramVideoActivity.class);
                            break;
                        case Constant.IMAGE_MODEL:
                            intent = new Intent(mActivity, ProgramImageActivity.class);
                            break;
                        case Constant.NAICHA_MODEL_1:
                            intent = new Intent(mActivity, ProgramTextActivity.class);
                            break;
                    }
                    if (intent != null) {
                        intent.putExtra("program_data", programBean);//节目数据
                        LogUtil.d(TAG, "programBean:" + programBean);
                        startActivityForResult(intent, mProgramRequestCode);
                    }
                } else if (baseProgram.getType().equals("2")) {
                    DayTaskBean dayTaskBean = (DayTaskBean) baseProgram;
                    Intent intent = new Intent(mActivity, DayTaskProgramActivity.class);
                    intent.putExtra("task_data", dayTaskBean);//节目数据
                    intent.putParcelableArrayListExtra("program_data", mProgramDatas);
                    LogUtil.d(TAG, "task_data:" + dayTaskBean);
                    startActivityForResult(intent, mProgramRequestCode);
                }
            }
        }

        @Override
        public void onItemProgressClickListen(View view, int position) {
            ProgramBean program = mProgramDatas.get(position);
            ArrayList<String> deviceMacs = program.getDeviceMacs();
            ArrayList<String> loadDeviceMacs = program.getLoadDeviceMacs();
            if (mTerminals == null) {
                String terminalJson = mSp.getString(Constant.MY_TERMINAL, "");
                LogUtil.d(TAG, "terminalJson:" + terminalJson);
                if (!TextUtils.isEmpty(terminalJson)) {
                    mTerminals = new Gson().fromJson(terminalJson, new TypeToken<ArrayList<TerminalBeanSimple>>() {
                    }.getType());
                }
            }
            LogUtil.d(TAG, "mTerminals:" + mTerminals);
            ArrayList<ProgramStateDialogItem> items = new ArrayList<>();
            for (String str : deviceMacs) {
                ProgramStateDialogItem item = new ProgramStateDialogItem();
                item.setLoad(false);
                setMacName(item, str);
                for (String load : loadDeviceMacs) {
                    if (str.equals(load)) {
                        item.setLoad(true);
                    }
                }
                items.add(item);
            }
            ProgramStateProgressDialog dialog = ProgramStateProgressDialog.getInstance(items);
            dialog.show(getFragmentManager(), "");
        }

        @Override
        public void onItemDeleteClickListen(View view, int position) {
            deleteProgramDialog(position);
        }
    };

    public ProgramStateDialogItem setMacName(ProgramStateDialogItem ret, String mac) {
        for (TerminalBeanSimple item : mTerminals) {
            if (item.getMac().equals(mac)) {
                ret.setName(item.getName());
                ret.setId(item.getId());
                return ret;
            }
        }
        return ret;
    }

    public void deleteProgramDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.hint))
                .setMessage(getResources().getString(R.string.sure_delete_program))
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BaseProgram baseProgram = mAdapter.getData(position);
                        if (baseProgram.getType().equals("1")) {
                            //删除节目
                            ProgramDao programDao = ProgramDao.getInstance(mActivity);
                            programDao.deleteProgram(mAdapter.getData(position).getTable_id() + "", mUserid);
                        } else if (baseProgram.getType().equals("2")) {
                            ProgramDao2 programDao2 = ProgramDao2.getInstance(mActivity);
                            programDao2.deleteData(baseProgram.getTable_id() + "");
                        }
                        initData();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public ProgramBean mProgram;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mProgramRequestCode && resultCode == mProgramResultCode) {
            //再发布
            ProgramBean body = (ProgramBean) data.getParcelableExtra("body");
            LogUtil.d(TAG, "onActivityResult:" + body);
            if (body != null) {
                mProgram = body;
                LogUtil.d(TAG, "获取返回的节目数据：" + mProgram);
                createProgramService();
                mProgramBean.setVisibility(View.VISIBLE);
                mTvComplete.setVisibility(View.GONE);
                ToastUtil.showToast(mActivity, getString(R.string.program_back_push), 3000);
            }
        }
    }

    /**
     * 创建后台服务上传节目
     */
    public void createProgramService() {
        Intent intent = new Intent(mActivity, ProgramPushService.class);
        mActivity.startService(intent);
        mActivity.bindService(intent, mCon, Context.BIND_AUTO_CREATE);
    }

    private boolean isBind;
    ServiceConnection mCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isBind = true;
            LogUtil.d(TAG, "mCon:" + "onServiceConnected");

            ProgramPushService.MyBinder myBinder = (ProgramPushService.MyBinder) iBinder;
            //更新数据库为已上传状态
            final SharedPreferences sp = mActivity.getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
            final ProgramDao baseHelper = ProgramDao.getInstance(mActivity);
            mProgram.setSign(MD5Util.getMD5(Constant.S_KEY + mProgram.getUserid()));
            myBinder.sendPushData(mProgram);
            myBinder.sendCompleteListener(new ProgramPushService.PushCompleteListener() {
                @Override
                public void updateSucceed(String programId) {
                    mProgramBean.setVisibility(View.GONE);
                    mTvComplete.setVisibility(View.VISIBLE);
                    //上传成功 解绑service
                    mActivity.unbindService(mCon);
                    isBind = false;

                    //更新数据库为已上传状态
                    LogUtil.d(TAG, "发布成功:" + mProgram);
                    try {
                        //保存到数据库
                        mProgram.setUpload_state("1");//上传成功状态
                        mProgram.setProgramId(programId);//更新节目ID
                        if (mProgram.getTable_id() == -1)//新建
                            baseHelper.addProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        else//再编辑
                            baseHelper.updateInProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        initData();//刷新界面
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showToast(mActivity, getString(R.string.push_succeed), 3000);
                }

                @Override
                public void updateDefeated() {
                    mProgramBean.setVisibility(View.GONE);
                    mTvComplete.setVisibility(View.VISIBLE);
                    //解绑service
                    mActivity.unbindService(mCon);
                    isBind = false;
                    //更新数据库为已上传状态
                    LogUtil.d(TAG, "发布失败:");
                    try {
                        //保存到数据库
                        mProgram.setUpload_state("-1");//上传失败状态
                        if (mProgram.getTable_id() == -1)//新建
                            baseHelper.addProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        else//再编辑
                            baseHelper.updateInProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        initData();//刷新界面
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showToast(mActivity, getString(R.string.push_defeated), 3000);
                }
            });
            //开始上传
            myBinder.startPush();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.d(TAG, "mCon:" + "onServiceDisconnected");
            isBind = false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy:" + isBind);

        if (isBind)
            mActivity.unbindService(mCon);
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {
        LogUtil.d(TAG, "hideProgress");
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
    }

    @Override
    public void loadDataSuccess(ProgramLoadStateResponse data, int requestTag) {
        LogUtil.d(TAG, "loadDataSuccess");

        if (data != null && data.getLoadDeviceMac().size() > 0) {
            ProgramBean bean = null;
            if (requestTag == (PROGRAM_LOAD_STATE_TAG + 0)) {
                //同步 以防用户删除节目
                if (mProgramDatas.size() > 0 && mProgramDatas.get(0).getProgramId().equals(loadProgramIds.get(0))) {
                    bean = mProgramDatas.get(0);
                    bean.setLoadDeviceMacs(data.getLoadDeviceMac());
                }
            } else if (requestTag == (PROGRAM_LOAD_STATE_TAG + 1)) {
                if (mProgramDatas.size() > 0 && mProgramDatas.get(1).getProgramId().equals(loadProgramIds.get(1))) {
                    bean = mProgramDatas.get(1);
                    bean.setLoadDeviceMacs(data.getLoadDeviceMac());
                }
            } else if (requestTag == (PROGRAM_LOAD_STATE_TAG + 2)) {
                if (mProgramDatas.size() > 0 && mProgramDatas.get(2).getProgramId().equals(loadProgramIds.get(2))) {
                    bean = mProgramDatas.get(2);
                    bean.setLoadDeviceMacs(data.getLoadDeviceMac());
                }
            }
            if (bean != null && bean.getLoadDeviceMacs().size() == bean.getDeviceMacs().size()) {
                //设备全部加载完成 更新数据库
                bean.setIsLoadSucceed("1");
                ProgramDao dao = ProgramDao.getInstance(mActivity);
                try {
                    dao.updateLoadState(bean.getTable_id() + "", "1", mUserid);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("数据库更新错误：" + e.getMessage());
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        LogUtil.d(TAG, "loadDataError");
        if (requestTag == (PROGRAM_LOAD_STATE_TAG + 0) && mRefresh.isRefreshing()) {
            ToastUtil.showToast(mActivity, e.getMessage(), 3000);
        }
    }
}
