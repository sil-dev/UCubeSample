package com.sil.ucubesdk.rest;

import retrofit2.Response;

/**
 * Created by aslesha.more on 4/21/2017.
 */

public interface ResponseListener {
    void onResponseSuccess(Response<ResponseParams> response);
    void onResponseFailure(Throwable throwable);
}
