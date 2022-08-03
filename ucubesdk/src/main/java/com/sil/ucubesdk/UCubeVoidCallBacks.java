package com.sil.ucubesdk;

import org.json.JSONObject;

public interface UCubeVoidCallBacks {
    void successCallback(JSONObject var1);

    void progressCallback(String var1);

    void failureCallback(JSONObject var1);
}
