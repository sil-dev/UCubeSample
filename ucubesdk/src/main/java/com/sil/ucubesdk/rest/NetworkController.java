package com.sil.ucubesdk.rest;

import com.sil.ucubesdk.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by monali solapure on 4/21/2017.
 */

public class NetworkController {

    //public static final String MAIN_URL = "http://114.79.162.173:5556/";
    public static String BASE_URL = BuildConfig.UCUBE_URL;

    private ApiListener apiService;

    public NetworkController() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiListener.class);

    }

    public static NetworkController getInstance() {
        return new NetworkController();
    }

    public void sendRequest(RequestParams requestParams, ResponseListener responseListener) {
        Call<ResponseParams> call = apiService.callService(requestParams);
        serverCall(call, responseListener);
    }

    private void serverCall(Call call, final ResponseListener responseListener) {
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                responseListener.onResponseSuccess(response);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                responseListener.onResponseFailure(t);
            }
        });
    }
}
