package com.jld.InformationRelease.util;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/11 17:49
 */
public class RetrofitManager {

    //短缓存有效期为1分钟
    public static final int CACHE_STALE_SHORT = 60;
    //长缓存有效期为7天
    public static final int CACHE_STALE_LONG = 60 * 60 * 24 * 7;
    //网络访问超时
    public static final int NETWORK_TIMEOUT = 60;
    private static RetrofitManager mRetrofitManager;
    private static RetrofitManager mRetrofitManager2;
    private Context mContext;
    private static OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    public static RetrofitManager getInstance(Context context) {
        if (mRetrofitManager == null)
            mRetrofitManager = new RetrofitManager(context,URLConstant.BASE_HTTP_URL);
        return mRetrofitManager;
    }
    public static RetrofitManager getInstanceDownload(Context context) {
        if (mRetrofitManager2 == null)
            mRetrofitManager2 = new RetrofitManager(context,URLConstant.BASE_HTTP_URL_DOWNLOAD);
        return mRetrofitManager2;
    }
    private RetrofitManager(Context context,String url) {
        mContext = context;
        initOkHttpClient();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//设置返回类型为RXJAVA
                .addConverterFactory(GsonConverterFactory.create()) //Gson解析
                .client(mOkHttpClient)
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    private void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClient == null) {
                    File cacheFile = new File(mContext.getCacheDir(), "HttpCache");
                    if (!cacheFile.exists())
                        cacheFile.mkdirs();
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(cacheFile, "HttpCache"),
                            1024 * 1024 * 10);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    // 云端响应头拦截器，用来配置缓存策略
    private Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetUtil.isNetworkConnected(mContext)) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetUtil.isNetworkConnected(mContext)) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder().header("Cache-Control", cacheControl)
                        .removeHeader("Pragma").build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_LONG)
                        .removeHeader("Pragma").build();
            }
        }
    };
}
