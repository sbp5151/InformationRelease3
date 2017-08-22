package com.jld.InformationRelease.view;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.jld.InformationRelease.util.LogUtil;

/**
 * Created by laucherish on 16/3/17.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static Context mApplicationContext;
    public static String mCurrentVersion;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "onCreate");
        mApplicationContext = this;
        registerActivityLifecycleCallbacks(mCallbacks);

        // 获取版本
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.jld.InformationRelease", 0);
            mCurrentVersion = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    //系统会在每个activity执行完生命周期后都会调用对应的方法
    ActivityLifecycleCallbacks mCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

            ComponentName name = activity.getComponentName();
            LogUtil.d(TAG, "activityName:" + name);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    // 获取ApplicationContext
    public static Context getContext() {
        return mApplicationContext;
    }
}
