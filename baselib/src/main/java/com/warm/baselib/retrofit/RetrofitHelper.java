package com.warm.baselib.retrofit;

import com.warm.baselib.BuildConfig;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：warm
 * 时间：2017-11-20 11:12
 * 描述：
 */

public class RetrofitHelper {
    private static final String BASE_URL = "http://api.douyutv.com/";
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    public static final String CACHE = "cache_http";


    public static void init() {
        if (okHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (okHttpClient == null) {
                    okHttpClient = provideOkHttpClient();
                }
            }
        }
        if (retrofit == null) {
            synchronized (RetrofitHelper.class) {
                if (retrofit == null) {
                    retrofit = provideRetrofit(okHttpClient);
                }
            }
        }
    }

    public static OkHttpClient provideOkHttpClient() {
        //设置Http缓存
        Cache cache = new Cache(new File(CACHE), 1024 * 1024 * 10);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
//                .addNetworkInterceptor(new CacheInterceptor())
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        return builder.build();
    }

    public static Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static <T> T provideApi(String baseUrl,Class<T> clazz) {
        if (retrofit == null) {
            init();
        }
        return retrofit.create(clazz);
    }


}