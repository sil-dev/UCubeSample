package com.sil.ucubesdk;

import org.json.JSONObject;

public interface UCubeCallBacks {
    void successCallback(JSONObject responseSuccess);

    void progressCallback(String string);

    void failureCallback(JSONObject responseSuccess);
}
