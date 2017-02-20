package com.spro.mapbaidu.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gqq on 2017/1/9.
 */

// 网络初始化的类
public class NetClient {


    private static NetClient mNetClient;
    public static final String BASE_URL = "http://admin.syfeicuiedu.com";
    private TreasureApi mTreasureApi;
    private final Retrofit mRetrofit;

    private NetClient(){
        // Retrofit的初始化
 
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()// 设置Gson的非严格模式
                .create();

        // 初始化Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

    public static synchronized NetClient getInstances(){
        if (mNetClient==null){
            mNetClient = new NetClient();
        }
        return mNetClient;
    }

    public TreasureApi getTreasureApi(){
        if (mTreasureApi==null){
            mTreasureApi = mRetrofit.create(TreasureApi.class);
        }
        return mTreasureApi;
    }
}
