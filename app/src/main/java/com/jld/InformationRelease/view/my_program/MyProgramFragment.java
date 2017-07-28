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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseProgram;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.ProgramStateDialogItem;
import com.jld.InformationRelease.bean.response_bean.ProgramPushStateResponse;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.dialog.ProgramStateProgressDialog;
import com.jld.InformationRelease.dialog.TerminalSelectDialog;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.ProgramLoadStatePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.my_program.adapter.MyProgramRecyclerAdapter;
import com.jld.InformationRelease.view.my_program.program_create.DayTaskProgramActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramImageActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramTextActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramVideoActivity;
import com.jld.InformationRelease.view.service.ProgramPushService;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

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
public class MyProgramFragment extends Fragment implements IViewListen<ProgramPushStateResponse> {

    private static final java.lang.String TAG = "MyProgramFragment";
    private MainActivity mActivity;
    public MyProgramRecyclerAdapter mAdapter;
    private ImageView mIv_add;
    private static final int NEW_PROGRAM = 101;//新建节目
    private static final int DAY_PROGRAM = 102;//每日任务
    private static final int INTER_CUT_PROGRAM = 103;//插播节目
    private static final int PROGRAM_LOAD_STATE_TAG = 104;//节目加载情况
    private static final int HIDE_REFRESH = 105;//隐藏refresh
    public static final int INIT_DATA = 106;//数据初始化
    public static final int PROGRAM_LOAD_TIME = 1000 * 60 * 5;//5分钟后不再获取节目加载情况
    private String mUserid;
    private ProgressBar mProgressBar;
    private ProgramLoadStatePresenter mPresenter;
    private ArrayList<String> loadProgramIds = new ArrayList<>();
    private ArrayList<Integer> loadProgramTags = new ArrayList<>();
    private ArrayList<TerminalBeanSimple> mTerminals;
    private ArrayList<ProgramBean> mProgramDatas = new ArrayList<>();
    private SharedPreferences mSp;
    private SwipeRefreshLayout mRefresh;
    private FloatingActionsMenu mBtn_menu;
    private Button mBtn_hide;
    private String[] mNames;
    /**
     * 插播类型
     */
    private String[] mIntercut_types = new String[3];

    //数据更新次数 默认为一次
    private int init_data_time = 1;
    //连续更新数据 间隔时间 默认2s
    private int init_data_interval = 3000;

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
                    intent.putParcelableArrayListExtra("program_datas", mProgramDatas);
                    startActivityForResult(intent, mProgramRequestCode);
                    break;
                case INTER_CUT_PROGRAM:
                    mBtn_hide.callOnClick();
                    break;
                case HIDE_REFRESH:
                    if (mRefresh.isRefreshing())
                        mRefresh.setRefreshing(false);
                    break;
                case INIT_DATA:
                    initData();
                    init_data_time--;
                    if (init_data_time > 0)
                        mHandler.sendEmptyMessageDelayed(INIT_DATA, init_data_interval);
                    else
                        init_data_time = 1;
                    break;
            }
        }
    };
    private ProgramPushService.MyBinder mPushProgramBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mIntercut_types[0] = getResources().getString(R.string.time_span);
        mIntercut_types[1] = getResources().getString(R.string.time_quantun);
        mIntercut_types[2] = getResources().getString(R.string.play_num);
        createProgramService();
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
        return view;
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
        TextView tvComplete = (TextView) title_view.findViewById(R.id.toolbar_complete);
        tvComplete.setVisibility(View.INVISIBLE);
        mProgressBar = (ProgressBar) title_view.findViewById(R.id.pb_program_upload);
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
                mHandler.sendEmptyMessageDelayed(HIDE_REFRESH, 1000);
            }
        });
        //SwipeMenuRecyclerView
        SwipeMenuRecyclerView recyclerView = (SwipeMenuRecyclerView) view.findViewById(R.id.program_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(mSwipeMenuItemClickListener);
        mAdapter = new MyProgramRecyclerAdapter(mActivity, mProgramDatas);
        mAdapter.setMyItemSelectClick(mMyItemClick);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 侧滑菜单 item点击监听
     */
    SwipeMenuItemClickListener mSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {//右侧菜单
                if (menuPosition == 0) {//推送
                    if (!sIsProgramUpload)
                        terminalSelect(adapterPosition);
                    else
                        ToastUtil.showToast(mActivity, getString(R.string.program_upload_ing), 3000);
                } else if (menuPosition == 1) {//删除
                    deleteProgramDialog(adapterPosition);
                }
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {//左侧菜单
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
            //推送 menu
            SwipeMenuItem menuItemPush = new SwipeMenuItem(mActivity)
                    .setBackground(R.drawable.swipe_menu_push)
                    .setHeight(height)
                    .setWidth(width)
                    .setText(getString(R.string.push))
                    .setTextColor(getResources().getColor(R.color.white));
            swipeRightMenu.addMenuItem(menuItemPush);
            //删除 menu
            SwipeMenuItem menuItemDelete = new SwipeMenuItem(mActivity)
                    .setBackground(R.drawable.swipe_menu_delete)
                    .setHeight(height)
                    .setWidth(width)
                    .setText(getString(R.string.delete))
                    .setTextColor(getResources().getColor(R.color.white));
            swipeRightMenu.addMenuItem(menuItemDelete);
        }
    };

    boolean isFirst = true;

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart");
        if (isFirst) {
            mRefresh.setRefreshing(true);
            mHandler.sendEmptyMessageDelayed(INIT_DATA, 500);
            mHandler.sendEmptyMessageDelayed(HIDE_REFRESH, 1500);
            isFirst = false;
        } else {
            initData();
        }
    }

    public void initData() {
        ProgramDao programDao = ProgramDao.getInstance(mActivity);
        try {
            mProgramDatas = programDao.getAllProgram(mUserid);
            LogUtil.d(TAG, "initData--mProgramDatas:" + mProgramDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mNames = new String[mProgramDatas.size()];
        for (int i = 0; i < mProgramDatas.size(); i++) {
            String name = mProgramDatas.get(i).getTab();
            mNames[i] = name;
        }
        //排序
        if (mProgramDatas.size() >= 2)
            Collections.sort(mProgramDatas, new SortByTime());
        //节目被加载情况
        if (mProgramDatas != null || mProgramDatas.size() > 0) {
            mAdapter.update(mProgramDatas);
            loadProgramState();
        }
        //获取终端设备
        String terminalJson = mSp.getString(Constant.MY_TERMINAL, "");
        LogUtil.d(TAG, "terminalJson:" + terminalJson);
        if (!TextUtils.isEmpty(terminalJson)) {
            mTerminals = new Gson().fromJson(terminalJson, new TypeToken<ArrayList<TerminalBeanSimple>>() {
            }.getType());
        }
    }

    /**
     * 终端选择
     */
    public void terminalSelect(final int position) {
        ArrayList<String> checkMac = mProgramDatas.get(position).getDeviceMacs();
        TerminalSelectDialog selectDialog = new TerminalSelectDialog(mActivity, checkMac, new TerminalSelectDialog.TerminalSelectListen() {
            @Override
            public void onSure() {
                uploadProgramData(mProgramDatas.get(position));
            }
        });
        selectDialog.show(getFragmentManager(), "select");
    }

    /**
     * 按时间降序方式排列节目列表
     */
    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            BaseProgram s1 = (BaseProgram) o1;
            BaseProgram s2 = (BaseProgram) o2;
            if (s1.getType().equals(s2.getType()))
                return TimeUtil.dateBack(s2.getTime()).compareTo(TimeUtil.dateBack(s1.getTime()));
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
            LogUtil.d(TAG, "timeGap:i--" + i + "--" +
                    (TimeUtil.toCurrentTimeGap(mProgramDatas.get(i).getTime())) / 1000 / 60);
            if (TimeUtil.toCurrentTimeGap(mProgramDatas.get(i).getTime()) > PROGRAM_LOAD_TIME)//只加载前面三个
                continue;
            //节目ID不为空并且设备未完全加载完成
            if (!TextUtils.isEmpty(mProgramDatas.get(i).getProgramId()) && mProgramDatas.get(i).getIsLoadSucceed().equals("0")) {
                if (mPresenter == null)
                    mPresenter = new ProgramLoadStatePresenter(this, mActivity);
                LogUtil.d(TAG, "节目ID加载情况:" + mProgramDatas.get(i).getProgramId());
                mPresenter.programLoadState(mProgramDatas.get(i).getProgramId(), PROGRAM_LOAD_STATE_TAG + i);
                loadProgramIds.add(mProgramDatas.get(i).getProgramId());
                loadProgramTags.add(PROGRAM_LOAD_STATE_TAG + i);
            }
        }
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
                case R.id.btn_create_program://创建节目
                    mHandler.sendEmptyMessageDelayed(NEW_PROGRAM, 200);
                    mBtn_menu.collapse();
                    break;
                case R.id.btn_day_task://每日任务
                    mHandler.sendEmptyMessageDelayed(DAY_PROGRAM, 200);
                    mBtn_menu.collapse();
                    break;
                case R.id.btn_inter_cut://假的插播任务
//                    mHandler.sendEmptyMessageDelayed(INTER_CUT_PROGRAM, 200);
                    mBtn_hide.callOnClick();
                    mBtn_menu.collapse();
                    break;
                case R.id.btn_hide_inter_cut://插播任务
                    boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
                    Dialog.Builder builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog) {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                            Toast.makeText(mActivity, "你选择的节目为：" + getSelectedValue() + " 此功能待开发", Toast.LENGTH_SHORT).show();
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
                    fragment.show(mActivity.getSupportFragmentManager(), null);
                    break;
            }
        }
    };

    MyProgramRecyclerAdapter.MyItemClick mMyItemClick = new MyProgramRecyclerAdapter.MyItemClick() {
        @Override
        public void onItemClickListen(View view, int position) {
            if (!mAdapter.getCompileState()) {//在编辑状态不让点击
                ProgramBean programBean = mProgramDatas.get(position);
                LogUtil.d(TAG, "programBean:" + programBean);
                if (programBean.getType().equals(Constant.PROGRAM_TYPE_COMMON)) {
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
                } else if (programBean.getType().equals(Constant.PROGRAM_TYPE_DAY)) {//每日任务
                    Intent intent = new Intent(mActivity, DayTaskProgramActivity.class);
                    ArrayList<ProgramBean> transferData = new ArrayList<>();
                    for (ProgramBean bean : mProgramDatas) {//提供普通节目供其布置任务
                        if (bean.getType().equals(Constant.PROGRAM_TYPE_COMMON)) {
                            transferData.add(bean);
                        }
                    }
                    intent.putParcelableArrayListExtra("program_datas", transferData);
                    intent.putExtra("program_data", programBean);
                    startActivityForResult(intent, mProgramRequestCode);
                }
            }
        }


        @Override
        public void onItemProgressClickListen(View view, int position) {

            ProgramBean program = mProgramDatas.get(position);
            LogUtil.d(TAG, "program:" + program);
            ArrayList<String> deviceMacs = program.getDeviceMacs();
            ArrayList<String> loadDeviceMacs = program.getLoadDeviceMacs();
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
        //被解绑的设备
        ret.setId("0");
        ret.setName(getString(R.string.have_unbunding));
        return ret;
    }

    public void deleteProgramDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.hint))
                .setMessage(getResources().getString(R.string.sure_delete_program))
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除节目
                        ProgramDao programDao = ProgramDao.getInstance(mActivity);
                        programDao.deleteProgram(mAdapter.getData(position).getTable_id() + "", mUserid);
                        initData();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mProgramRequestCode && resultCode == mProgramResultCode) {
            //再发布
            ProgramBean body = data.getParcelableExtra("body");
            LogUtil.d(TAG, "onActivityResult:" + body);
            if (body != null) {
                uploadProgramData(body);
            }
        }
    }

    public static boolean sIsProgramUpload = false;

    /**
     * 上传节目数据
     */
    public void uploadProgramData(final ProgramBean program) {
        LogUtil.d(TAG, "上传节目数据:" + program);
        sIsProgramUpload = true;
        //progressbar
        mProgressBar.setVisibility(View.VISIBLE);
        ToastUtil.showToast(mActivity, getString(R.string.program_back_push), 3000);
        //更新节目最新时间
        program.setTime(TimeUtil.getTodayDateTime());
        //更新数据库为已上传状态
        program.setSign(MD5Util.getMD5(Constant.S_KEY + program.getUserid()));
        //上传结果监听
        mPushProgramBinder.sendCompleteListener(new ProgramPushService.PushCompleteListener() {
            @Override
            public void pushSucceed(String programId) {//节目上传成功
                sIsProgramUpload = false;
                LogUtil.d(TAG, "节目上传成功:" + program);
                mProgressBar.setVisibility(View.GONE);
                //更新数据库为已上传状态
                try {
                    //保存到数据库
                    program.setUpload_state("1");//上传成功状态
                    program.setProgramId(programId);//更新节目ID
                    saveProgramDatabase(program);
                    mHandler.sendEmptyMessageDelayed(INIT_DATA, init_data_interval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtil.showToast(mActivity, getString(R.string.push_succeed), 3000);
            }

            @Override
            public void pushDefeated() {
                sIsProgramUpload = false;
                mProgressBar.setVisibility(View.GONE);
                //更新数据库为已上传状态
                LogUtil.d(TAG, "发布失败:");
                try {
                    //保存到数据库
                    program.setUpload_state("-1");//上传失败状态
                    saveProgramDatabase(program);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtil.showToast(mActivity, getString(R.string.push_defeated), 3000);
            }

            @Override
            public void uploadSucceed(ProgramBean programBean) {
                if (programBean != null) {
                    try {
                        //保存到数据库
                        programBean.setUpload_state("1");//上传成功状态
                        programBean.setType("1");//普通任务
                        saveProgramDatabase(programBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //开始上传
        if (program.getType().equals("2")) {
            mPushProgramBinder.uploadDayTask(program, mProgramDatas);
        } else if (program.getType().equals("1"))
            mPushProgramBinder.startPush(program);
        else
            LogUtil.e("上传数据未匹配");
    }

    /**
     * 将节目保存到数据库
     */
    public void saveProgramDatabase(ProgramBean program) throws Exception {
        LogUtil.d(TAG, "数据库保存节目数据：" + program);
        final ProgramDao baseHelper = ProgramDao.getInstance(mActivity);
        if (program.getTable_id() == -1)//新建
            baseHelper.addProgram(program, mSp.getString(UserConstant.USER_ID, ""));
        else//再编辑
            baseHelper.updateInProgram(program, mSp.getString(UserConstant.USER_ID, ""));
        initData();//刷新界面
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
            mPushProgramBinder = (ProgramPushService.MyBinder) iBinder;
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
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop:");
        if (mBtn_menu.isExpanded())
            mBtn_menu.collapse();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.d(TAG, "onHiddenChanged:" + hidden);
        if (hidden) {
            if (mBtn_menu.isExpanded())
                mBtn_menu.collapse();
        }
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
    public void loadDataSuccess(ProgramPushStateResponse data, int requestTag) {
        LogUtil.d(TAG, "loadDataSuccess:" + data);
        LogUtil.d(TAG, "PROGRAM_LOAD_STATE_TAG:" + requestTag);
        if (loadProgramTags.contains(requestTag)) {
            if (data != null && data.getItem() != null && data.getItem().size() > 0) {
                //已加载成功的Mac地址
                ArrayList<ProgramPushStateResponse.PushStateItem> items = data.getItem();
                //对应节目数据
                ProgramBean programBean = mProgramDatas.get(requestTag - PROGRAM_LOAD_STATE_TAG);
                LogUtil.d(TAG, "load_programBean:" + programBean);
                LogUtil.d(TAG, "items:" + items);
                //同步 以防用户删除节目
                if (loadProgramIds.contains(programBean.getProgramId())) {
                    //添加已加载节目的设备mac
                    ArrayList<String> loadDeviceMacs = programBean.getLoadDeviceMacs();
                    for (ProgramPushStateResponse.PushStateItem item : items) {
                        if (!loadDeviceMacs.contains(item.getDeviceMacs()) && item.getTime().equals(programBean.getTime()))
                            loadDeviceMacs.add(item.getDeviceMacs());
                    }
                    programBean.setLoadDeviceMacs(loadDeviceMacs);
                    //如果设备全部加载完成
                    if (programBean.getLoadDeviceMacs().size() == programBean.getDeviceMacs().size())
                        programBean.setIsLoadSucceed("1");
                    //更新数据库
                    ProgramDao dao = ProgramDao.getInstance(mActivity);
                    try {
                        dao.updateLoadState(programBean.getTable_id() + "", programBean.getIsLoadSucceed(), programBean.getLoadDeviceMacs());
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("数据库更新错误：" + e.getMessage());
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
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
