package com.jld.InformationRelease.view.my_program;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseRecyclerViewAdapterClick;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_terminal.ProgramCompileActivity;

import org.json.JSONException;

import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramRequestCode;
import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * 节目列表
 */
public class MyProgramFragment extends Fragment {

    private static final java.lang.String TAG = "MyProgramFragment";
    private MainActivity mActivity;
    private ArrayList<ProgramBean> mAllProgram;
    public MyProgramRecyclerAdapter mAdapter;
    private ImageView mIv_add;
    public static final int NEW_PROGRAM = 0x11;//新建节目
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NEW_PROGRAM:
                    Intent intent = new Intent(mActivity, ProgramCompileActivity.class);
                    intent.putParcelableArrayListExtra("terminal_data", mActivity.mTerminal_fragment.mAdapter.getData());//终端数据
                    startActivityForResult(intent, mProgramRequestCode);
                    break;
            }
        }
    };
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.d(TAG, "hidden:" + hidden);
        initData();
        if (mAllProgram != null)
            mAdapter.update(mAllProgram);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_model, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private void initView(View view) {
        //title
        View title_view = view.findViewById(R.id.my_progarm_title);
        ImageButton menu = (ImageButton) title_view.findViewById(R.id.toolbar_left);
        TextView tvComplete = (TextView) title_view.findViewById(R.id.toolbar_complete);
        tvComplete.setOnClickListener(mOnClickListener);
        title_view.findViewById(R.id.toolbar_push).setVisibility(View.GONE);
        menu.setOnClickListener(mOnClickListener);
        TextView title_content = (TextView) title_view.findViewById(R.id.toolbar_title);
        title_content.setText(getString(R.string.program_list));

        //RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.terminal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyProgramRecyclerAdapter(mActivity, mAllProgram);
        mAdapter.setMyItemSelectClick(mMyItemClick);
        mRecyclerView.setAdapter(mAdapter);

        //add
        mIv_add = (ImageView) view.findViewById(R.id.imagev_add_trouteam);
        mIv_add.setOnClickListener(mOnClickListener);
    }

    public void initData() {
        SharedPreferences sp = mActivity.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
        String userid = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userid)) {
            ToastUtil.showToast(mActivity, getResources().getString(R.string.please_login), 3000);
            mActivity.toActivity(LoginActivity.class);
        }
        ProgramDao programDao = ProgramDao.getInstance(mActivity, userid);
        try {
            mAllProgram = programDao.getAllProgram();
            LogUtil.d(TAG, "mAllProgram:" + mAllProgram);
        } catch (JSONException e) {
            ToastUtil.showToast(mActivity, getResources().getString(R.string.read_data_error), 3000);
            e.printStackTrace();
        }
        if (mAllProgram != null)
            mAdapter.update(mAllProgram);
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
                case R.id.toolbar_complete://编辑
                    break;
                case R.id.imagev_add_trouteam://新建节目单
                    togetherRun(mIv_add, 270);
                    mHandler.sendEmptyMessageDelayed(NEW_PROGRAM,270);
                    break;
            }
        }
    };
    BaseRecyclerViewAdapterClick.MyItemClick mMyItemClick = new BaseRecyclerViewAdapterClick.MyItemClick() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(mActivity, ProgramCompileActivity.class);
            intent.putExtra("program_data", mAllProgram.get(position));//节目数据
            intent.putParcelableArrayListExtra("terminal_data", mActivity.mTerminal_fragment.mAdapter.getData());//终端数据
            startActivityForResult(intent, mProgramRequestCode);
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mProgramRequestCode && resultCode == mProgramResultCode) {
            //再发布
            ProgramBean body = (ProgramBean) data.getSerializableExtra("body");
            if (body != null) {
                mActivity.mProgram = body;
                mActivity.createProgramService();
            }
        }
    }

    /**
     * 按钮动画
     *
     * @param imageView
     * @param duration
     */
    public static void togetherRun(ImageView imageView, int duration) {
        if (null == imageView) {
            return;
        }
        if (duration <= 0) {
            duration = 400;
        }
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(imageView, "scaleX",
                1.0f, 0.8f, 1.1f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(imageView, "scaleY",
                1.0f, 0.8f, 1.1f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(duration);
        animSet.setInterpolator(new LinearInterpolator());
        //两个动画同时执行
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }
}
