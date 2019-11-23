package com.jadebyte.jadeplayer.main.shazam.api;


import android.util.Log;

import com.jadebyte.jadeplayer.main.shazam.model.ResultTrack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AuddAPI {

    private static final String TAG = "APILog";

    public static class Method {
        public static final String RECOGNIZE = "recognize";
        public static final String RECOGNIZE_WITH_OFFSET = "recognizeWithOffset";
    }

    private final String URL = "https://api.audd.io/";
    private final String API_TOKEN_TEST = "test";
    private final String API_TOKEN = "67c0097c4d963d4ea359056ff8a188df";

    public ResultTrack recognizeVoice(final File file, final boolean isHumming) {
        ResultTrack track = null;
        try {
            String method;
            JSONObject response = upload(file, method = isHumming ? Method.RECOGNIZE_WITH_OFFSET : Method.RECOGNIZE, API_TOKEN_TEST);
            Log.e(TAG, method + " = " + response);
            if (response.has("result") && !response.isNull("result")) {
                track = new ResultTrack(response.getJSONObject("result"));
            } else {
                response = upload(file, method = isHumming ? Method.RECOGNIZE_WITH_OFFSET : Method.RECOGNIZE, API_TOKEN);
                Log.e(TAG, method + " = " + response);
                if (response.has("result") && !response.isNull("result")) {
                    track = new ResultTrack(response.getJSONObject("result"));
                }
            }
        } catch (Throwable ignored) {
        }
        return track;
    }


    public JSONObject upload(File file, String method, String apiToken) throws JSONException {
        return new JSONObject(Network.uploadFile(URL + "?method=" + method + "&api_token=" + apiToken, file, "file", 0));
    }

}
