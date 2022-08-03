package com.sil.ucubesdk.rest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by aslesha.more on 4/21/2017.
 */

public interface ApiListener {
    @POST("MobileReq")
    Call<ResponseParams> callService(@Body RequestParams jsonReq);
}
