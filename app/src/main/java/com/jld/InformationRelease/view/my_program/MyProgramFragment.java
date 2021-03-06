package com.jld.InformationRelease.view.my_program;


import android.Manifest;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseProgram;
import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.ProgramStateDialogItem;
import com.jld.InformationRelease.bean.response_bean.DeviceBeanSimple;
import com.jld.InformationRelease.bean.response_bean.ProgramPushStateResponse;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.dialog.ProgramStateProgressDialog;
import com.jld.InformationRelease.dialog.SpotsProgramDialog;
import com.jld.InformationRelease.dialog.TerminalSelectDialog;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.ProgramLoadStatePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_program.adapter.MyProgramRecyclerAdapter;
import com.jld.InformationRelease.view.my_program.program_create.DayTaskProgramActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramImageActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramTextActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramVideoActivity;
import com.jld.InformationRelease.view.service.ProgramPushService;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Comparator;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.content.Context.MODE_PRIVATE;
import static com.jld.InformationRelease.view.my_terminal.MyDeviceFragment.PROGRAM_REQUEST_CODE;
import static com.jld.InformationRelease.view.my_terminal.MyDeviceFragment.PROGRAM_RESULT_CODE;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * 节目列表
 */
@RuntimePermissions
public class MyProgramFragment extends Fragment implements IViewListen<ProgramPushStateResponse> {

    private static final java.lang.String TAG = "MyProgramFragment";
    private MainActivity mActivity;
    public MyProgramRecyclerAdapter mAdapter;
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
    private ArrayList<DeviceBeanSimple> mTerminals;
    private ArrayList<ProgramBean> mProgramDatas = new ArrayList<>();
    private SharedPreferences mSp;
    private SwipeRefreshLayout mRefresh;
    private FloatingActionsMenu mBtn_menu;
    private Button mBtn_hide;
    private ArrayList<String> mNames = new ArrayList<>();
    private boolean isSwipeRefresh = true;
    /**
     * 插播类型
     */
    private String[] mIntercut_types = new String[3];

    //数据更新次数 默认为一次
    private int init_data_time = 1;
    //连续更新数据 间隔时间 默认2s
    private int init_data_interval = 5000;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            super.handleMessage(msg);
            switch (msg.what) {
                case NEW_PROGRAM:
                    intent = new Intent(mActivity, SelectModelActivity.class);
                    intent.putParcelableArrayListExtra("terminal_data", mActivity.mTerminal_fragment.mAdapter.getData());//终端数据
                    startActivityForResult(intent, PROGRAM_REQUEST_CODE);
                    break;
                case DAY_PROGRAM:
                    intent = new Intent(mActivity, DayTaskProgramActivity.class);
                    intent.putParcelableArrayListExtra("program_datas", mProgramDatas);
                    startActivityForResult(intent, PROGRAM_REQUEST_CODE);
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
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mIntercut_types[0] = getResources().getString(R.string.time_span);
        mIntercut_types[1] = getResources().getString(R.string.time_quantun);
        mIntercut_types[2] = getResources().getString(R.string.play_num);
        mSp = mActivity.getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        createProgramService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_my_program, container, false);
        mUserid = mSp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(mUserid)) {//需要登录
            ToastUtil.showToast(mActivity, getResources().getString(R.string.please_login), 3000);
            mActivity.toActivity(LoginActivity.class);
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
                isSwipeRefresh = true;
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
        Log.d(TAG, "onStart");
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
            Log.d(TAG, "initData--mProgramDatas:" + mProgramDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //排序
        //if (mProgramDatas.size() >= 2)
          //  Collections.sort(mProgramDatas, new SortByTime());
        mNames.clear();
        for (int i = 0; i < mProgramDatas.size(); i++) {
            if (mProgramDatas.get(i).getType().equals(Constant.PROGRAM_TYPE_COMMON)) {
                String name = mProgramDatas.get(i).getTab();
                mNames.add(name);
            }
        }
        //节目被加载情况
        if (mProgramDatas != null || mProgramDatas.size() > 0) {
            mAdapter.update(mProgramDatas);
            loadProgramState();
        }
        //获取终端设备
        String terminalJson = mSp.getString(Constant.MY_TERMINAL, "");
        Log.d(TAG, "terminalJson:" + terminalJson);
        if (!TextUtils.isEmpty(terminalJson)) {
            mTerminals = new Gson().fromJson(terminalJson, new TypeToken<ArrayList<DeviceBeanSimple>>() {
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
            public void onSure(ArrayList<String> selectMac) {
                Log.d(TAG, "terminalSelect:" + selectMac);
                mProgramDatas.get(position).getLoadDeviceMacs().clear();
                mProgramDatas.get(position).setIsLoadSucceed("0");
                MyProgramFragmentPermissionsDispatcher.uploadProgramDataWithCheck(
                        MyProgramFragment.this, mProgramDatas.get(position));
            }
        });
        selectDialog.show(getFragmentManager(), "select");
    }

    /**
     * 插播推送
     *
     * @param position
     */
    public void terminalSelect2(final int position, final ProgramBean pushProgramBean) {
        ArrayList<String> checkMac = mProgramDatas.get(position).getDeviceMacs();
        ArrayList<String> new_mac = new ArrayList<>();
        new_mac.addAll(checkMac);
        TerminalSelectDialog selectDialog = new TerminalSelectDialog(mActivity, new_mac, new TerminalSelectDialog.TerminalSelectListen() {
            @Override
            public void onSure(ArrayList<String> selectMac) {
                Log.d(TAG, "terminalSelect:" + selectMac);
                pushProgramBean.setDeviceMacs(selectMac);
                MyProgramFragmentPermissionsDispatcher.uploadProgramDataWithCheck(MyProgramFragment.this, pushProgramBean);
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
                if (Integer.parseInt(s1.getType()) < Integer.parseInt(s2.getType()))
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
            Log.d(TAG, "timeGap:i--" + i + "--" +
                    (TimeUtil.toCurrentTimeGap(mProgramDatas.get(i).getTime())) / 1000 / 60);
            if (TimeUtil.toCurrentTimeGap(mProgramDatas.get(i).getTime()) > PROGRAM_LOAD_TIME)//前五分钟
                continue;
            //节目ID不为空并且设备未完全加载完成
            if (!TextUtils.isEmpty(mProgramDatas.get(i).getProgramId()) && mProgramDatas.get(i).getIsLoadSucceed().equals("0")) {
                if (mPresenter == null)
                    mPresenter = new ProgramLoadStatePresenter(this, mActivity);
                Log.d(TAG, "节目ID加载情况:" + mProgramDatas.get(i).getProgramId());
                mPresenter.programLoadState(mProgramDatas.get(i).getProgramId(), PROGRAM_LOAD_STATE_TAG + i);
                loadProgramIds.add(mProgramDatas.get(i).getProgramId());
                loadProgramTags.add(PROGRAM_LOAD_STATE_TAG + i);
            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void createProgram() {
        mHandler.sendEmptyMessageDelayed(NEW_PROGRAM, 200);
        mBtn_menu.collapse();
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
                    MyProgramFragmentPermissionsDispatcher.createProgramWithCheck(MyProgramFragment.this);
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
                    SpotsProgramDialog spotsDialog = new SpotsProgramDialog(getActivity(), mNames, new SpotsProgramDialog.OnUrgencyProgramListen() {
                        @Override
                        public void onPush(int selectProgramPosition, String play_type, String num1, String num2) {
                            Log.d(TAG, "selectProgramPosition:" + selectProgramPosition + "\n\r"
                                    + "play_type:" + play_type + "\n\r" + "num1:" + num1 + "\n\r" + "num2:" + num2);
                            Log.d(TAG, "---mProgramDatas1:" + mProgramDatas);
                            ProgramBean program = new ProgramBean();
                            program.setType(Constant.PROGRAM_TYPE_URGENCY);
                            program.setUserid(mUserid);
                            ProgramBean selectProgram = mProgramDatas.get(selectProgramPosition);
                            program.setTab(selectProgram.getTab() + "-" + getString(R.string.spots));
                            DayTaskItem item = new DayTaskItem();
                            item.setType(play_type);
                            item.setStateTime(num1);
                            item.setStopTime(num2);
                            item.setProgramLocalId(selectProgram.getProgramId());
                            item.setProgramTabId(selectProgram.getTable_id() + "");
                            ArrayList<DayTaskItem> dayTaskItems = new ArrayList<>();
                            dayTaskItems.add(item);
                            program.setDayProgram(dayTaskItems);
                            program.setSign(MD5Util.getMD5(Constant.S_KEY + mUserid));
                            Log.d(TAG, "---mProgramDatas1.1:" + mProgramDatas);
                            terminalSelect2(selectProgramPosition, program);
                        }
                    });
                    spotsDialog.show(getFragmentManager(), "dialog");
                    break;
            }
        }
    };

    MyProgramRecyclerAdapter.MyItemClick mMyItemClick = new MyProgramRecyclerAdapter.MyItemClick() {
        @Override
        public void onItemClickListen(View view, int position) {
            if (!mAdapter.getCompileState()) {//在编辑状态不让点击
                ProgramBean programBean = mProgramDatas.get(position);
                Log.d(TAG, "programBean:" + programBean);
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
                        Log.d(TAG, "programBean:" + programBean);
                        startActivityForResult(intent, PROGRAM_REQUEST_CODE);
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
                    startActivityForResult(intent, PROGRAM_REQUEST_CODE);
                }
            }
        }


        @Override
        public void onItemProgressClickListen(View view, int position) {

            ProgramBean program = mProgramDatas.get(position);
            Log.d(TAG, "program:" + program);
            ArrayList<String> deviceMacs = program.getDeviceMacs();
            ArrayList<String> loadDeviceMacs = program.getLoadDeviceMacs();
            Log.d(TAG, "mTerminals:" + mTerminals);
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
        for (DeviceBeanSimple item : mTerminals) {
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
        if (requestCode == PROGRAM_REQUEST_CODE && resultCode == PROGRAM_RESULT_CODE) {
            //再发布
            ProgramBean body = data.getParcelableExtra("body");
            if (body != null) {
                MyProgramFragmentPermissionsDispatcher.uploadProgramDataWithCheck(MyProgramFragment.this, body);
            }
        }
    }

    public static boolean sIsProgramUpload = false;

    /**
     * 上传节目数据
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void uploadProgramData(final ProgramBean program) {
        Log.d(TAG, "上传节目数据:" + program);
        sIsProgramUpload = true;
        //progressbar
        mProgressBar.setVisibility(View.VISIBLE);
        ToastUtil.showToast(mActivity, getString(R.string.program_back_push), 3000);
        //更新节目最新时间
        program.setTime(System.currentTimeMillis()+"");
        //更新数据库为已上传状态
        program.setSign(MD5Util.getMD5(Constant.S_KEY + program.getUserid()));
        //上传结果监听
        mPushProgramBinder.sendCompleteListener(new ProgramPushService.PushCompleteListener() {
            @Override
            public void pushSucceed(String programId) {//节目上传成功
                sIsProgramUpload = false;
                Log.d(TAG, "pushSucceed 节目上传成功:" + program);
                mProgressBar.setVisibility(View.GONE);
                //更新数据库为已上传状态
                try {
                    //保存到数据库
                    program.setUpload_state(Constant.UPLOAD_STATE_SUCCESS);//上传成功状态
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
                Log.e(TAG, "pushDefeated  发布失败:");
                try {
                    //保存到数据库
                    program.setUpload_state(Constant.UPLOAD_STATE_FAIL);//上传失败状态
                    saveProgramDatabase(program);
                    mHandler.sendEmptyMessageDelayed(INIT_DATA, init_data_interval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtil.showToast(mActivity, getString(R.string.push_defeated), 3000);
            }

            @Override
            public void uploadSucceed(ProgramBean programBean) {
                Log.d(TAG, "uploadSucceed:" + programBean);
                if (programBean != null) {
                    try {
                        //保存到数据库
                        programBean.setUpload_state(Constant.UPLOAD_STATE_SUCCESS);//上传成功状态
                        programBean.setType(Constant.PROGRAM_TYPE_COMMON);//普通任务
                        saveProgramDatabase(programBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //开始上传
        if (program.getType().equals(Constant.PROGRAM_TYPE_DAY) || program.getType().equals(Constant.PROGRAM_TYPE_URGENCY)) {
            Log.d(TAG, "---mProgramDatas3:" + mProgramDatas);
            mPushProgramBinder.uploadDayTask(program, mProgramDatas);
        } else if (program.getType().equals(Constant.PROGRAM_TYPE_COMMON))
            mPushProgramBinder.startPush(program);
        else
            Log.e(TAG,"上传数据未匹配");
    }

    /**
     * 将节目保存到数据库
     */
    public void saveProgramDatabase(ProgramBean program) throws Exception {
        Log.d(TAG, "数据库保存节目数据：" + program);
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
            Log.d(TAG, "mCon:" + "onServiceConnected");
            mPushProgramBinder = (ProgramPushService.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "mCon:" + "onServiceDisconnected");
            isBind = false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy:" + isBind);
        if (isBind)
            mActivity.unbindService(mCon);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop:");
        if (mBtn_menu.isExpanded())
            mBtn_menu.collapse();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged:" + hidden);
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
        Log.d(TAG, "hideProgress");
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
    }

    @Override
    public void loadDataSuccess(ProgramPushStateResponse data, int requestTag) {
        Log.d(TAG, "loadDataSuccess:" + data);
        Log.d(TAG, "PROGRAM_LOAD_STATE_TAG:" + requestTag);
        if (loadProgramTags.contains(requestTag)) {
            if (data != null && data.getItem() != null && data.getItem().size() > 0) {
                //已加载成功的Mac地址
                ArrayList<ProgramPushStateResponse.PushStateItem> items = data.getItem();
                //对应节目数据
                ProgramBean programBean = mProgramDatas.get(requestTag - PROGRAM_LOAD_STATE_TAG);
                Log.d(TAG, "load_programBean:" + programBean);
                Log.d(TAG, "items:" + items);
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
                        Log.e(TAG,"数据库更新错误：" + e.getMessage());
                    }
                    mAdapter.notifyDataSetChanged();
                    if(isSwipeRefresh){
                        ToastUtil.showToast(mActivity, getString(R.string.refresh_succeed), 3000);
                        isSwipeRefresh = false;
                    }
                }
            }
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        Log.d(TAG, "loadDataError");
        if (loadProgramTags.contains(requestTag)) {
            ToastUtil.showToast(mActivity, e.getMessage(), 3000);
            isSwipeRefresh = false;
        }
    }
}
