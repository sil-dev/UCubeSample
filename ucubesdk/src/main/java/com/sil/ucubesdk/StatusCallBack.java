package com.sil.ucubesdk;

import com.sil.ucubesdk.POJO.UCubeRequest;

import org.json.JSONObject;

public interface StatusCallBack {

    void successCallback(JSONObject responseSuccess);

    void failureCallback(JSONObject responseSuccess);

    void exceptionCallback(String exceptionMessage, UCubeRequest uCubeRequest, JSONObject respObj);
}


