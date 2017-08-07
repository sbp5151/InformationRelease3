package com.jld.InformationRelease.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.jld.InformationRelease.MyApplication;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.ijk.common.PlayerManager;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.view.MainActivity;

import java.io.File;
import java.util.ArrayList;

public class IjkVideoFragment extends Fragment implements PlayerManager.PlayerStateListener, PlayerManager.OnUITouchListen {

    public static final String TAG = "IjkVideoFragment";
    private ArrayList<String> mUrl;
    private int playerNum = 0;
    private MainActivity activity;
    private HttpProxyCacheServer httpProxyCacheServer;
    private PlayerManager playerManager;

    Handler mHandler = new Handler();
    Runnable hiedUiRun = new Runnable() {
        @Override
        public void run() {
            activity.hiedSystemUi();
        }
    };
    private ProgressDialog mProgressDialog;

    public static IjkVideoFragment getInstance(ArrayList<String> videoPath) {
        IjkVideoFragment ijkVideoFragment = new IjkVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("player_url", videoPath);
        ijkVideoFragment.setArguments(bundle);
        return ijkVideoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUrl = bundle.getStringArrayList("player_url");
        } else {
            try {
                throw new Exception("ProgramFragment需要传入参数");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ijk_video, container, false);
        httpProxyCacheServer = MyApplication.getProxy(activity);
        LogUtil.d(TAG, "onCreateView:" + mUrl);
        if (mUrl != null && mUrl.size() > 0) {
            initPlayer(view);
            mProgressDialog = ProgressDialog.show(activity, "提示", "视频正在缓冲...");

        }
        return view;
    }

    private void initPlayer(View view) {
        playerManager = new PlayerManager(activity, view);
        playerManager.setScaleType(PlayerManager.SCALETYPE_FITXY);
        playerManager.setPlayerStateListener(this);
        playerManager.setFullScreenOnly(true);
        playerManager.playInFullScreen(true);
        playerManager.setOnUITouchListen(this);
        playerManager.play(mUrl.get(playerNum));
        httpProxyCacheServer.registerCacheListener(cacheListener, mUrl.get(playerNum));
    }

    CacheListener cacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            LogUtil.d(TAG, "onCacheAvailable:" + percentsAvailable);
        }
    };

    private void next() {
        playerNum++;
        LogUtil.d(TAG, "onCompletion:" + playerNum);
        if (playerNum >= mUrl.size())
            playerNum = 0;
        playerManager.play(mUrl.get(playerNum));
        boolean cached = httpProxyCacheServer.isCached(mUrl.get(playerNum));
        if (!cached) {
            httpProxyCacheServer.unregisterCacheListener(cacheListener);
            httpProxyCacheServer.registerCacheListener(cacheListener, mUrl.get(playerNum));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        if (cacheListener != null)
            httpProxyCacheServer.unregisterCacheListener(cacheListener);
        if (playerManager != null)
            playerManager.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onComplete() {
        LogUtil.d(TAG, "onComplete");
        next();
    }

    @Override
    public void onError() {
        Toast.makeText(activity, getString(R.string.player_error), Toast.LENGTH_SHORT).show();
        LogUtil.d(TAG, "onError:" + mUrl.get(playerNum));
        next();
    }

    @Override
    public void onLoading() {
        LogUtil.d(TAG, "onLoading");
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    @Override
    public void onPlay() {
        LogUtil.d(TAG, "onPlay");
        activity.hiedSystemUi();
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onSystemUiTouch() {
        LogUtil.d(TAG, "onSystemUiTouch");
        mHandler.removeCallbacks(hiedUiRun);
        mHandler.postDelayed(hiedUiRun, 2500);
    }
}
