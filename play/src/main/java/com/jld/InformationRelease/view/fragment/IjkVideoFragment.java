package com.jld.InformationRelease.view.fragment;

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
import com.jld.InformationRelease.dialog.MyProgressDialog;
import com.jld.InformationRelease.ijk.common.PlayerManager;
import com.jld.InformationRelease.util.L;
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
    private MyProgressDialog mProgressDialog;

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
        L.d(TAG, "onCreateView:" + mUrl);
        if (mUrl != null && mUrl.size() > 0) {
            initPlayer(view);
            mProgressDialog = new MyProgressDialog(activity, "");

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
            L.d(TAG, "onCacheAvailable:" + percentsAvailable);
        }
    };

    private void next() {
        playerNum++;
        L.d(TAG, "onCompletion:" + playerNum);
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
        L.d(TAG, "onDestroy");
        if (cacheListener != null)
            httpProxyCacheServer.unregisterCacheListener(cacheListener);
        if (playerManager != null)
            playerManager.onDestroy();
        if (mProgressDialog != null && mProgressDialog.getDialog() != null && mProgressDialog.getDialog().isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onComplete() {
        L.d(TAG, "onComplete");
        next();
    }

    @Override
    public void onError() {
        Toast.makeText(activity, getString(R.string.player_error), Toast.LENGTH_SHORT).show();
        L.d(TAG, "onError:" + mUrl.get(playerNum));
        next();
    }

    @Override
    public void onLoading() {
        L.d(TAG, "onLoading");
        if (mProgressDialog != null && mProgressDialog.getDialog() != null&& !mProgressDialog.getDialog().isShowing())
            mProgressDialog.show(activity.getFragmentManager(), "dialog");
    }

    @Override
    public void onPlay() {
        L.d(TAG, "onPlay");
        activity.hiedSystemUi();
        if (mProgressDialog != null&& mProgressDialog.getDialog() != null && mProgressDialog.getDialog().isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onSystemUiTouch() {
        L.d(TAG, "onSystemUiTouch");
        mHandler.removeCallbacks(hiedUiRun);
        mHandler.postDelayed(hiedUiRun, 2500);
    }
}
