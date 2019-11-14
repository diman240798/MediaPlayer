package com.jadebyte.jadeplayer.main.shazam.api;


import android.util.Log;

import com.jadebyte.jadeplayer.main.shazam.model.ResultTrack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class API {

    private static final String TAG = "APILog";

    public static class Method {
        public static final String RECOGNIZE = "recognize";
        public static final String RECOGNIZE_WITH_OFFSET = "recognizeWithOffset";
    }

    private static volatile API Instance = null;
    private static final String URL = "https://api.audd.io/";
    private static final String API_TOKEN = "test";
    public static final DispatchQueue thread = new DispatchQueue("API");
    private static final int group_id = 138792192;

    public ResultTrack recognizeVoice(final File file, final boolean isHumming) {
        final ResultTrack[] track = {null};
        thread.postRunnable(new Runnable() {
            @Override
            public void run() {

                try {
                    String method;
                    JSONObject response = upload(file, method = isHumming ? Method.RECOGNIZE_WITH_OFFSET : Method.RECOGNIZE);
                    Log.e(TAG, method + " = " + response);
                    if (response.has("result") && !response.isNull("result")) {
                        track[0] = new ResultTrack(response.getJSONObject("result"));
                    }
                } catch (Throwable ignored) { }
            }
        });
        return track[0];
    }


    public JSONObject upload(File file, String method) throws JSONException {
        return new JSONObject(Network.uploadFile(URL + "?method=" + method + "&api_token=" + API_TOKEN, file, "file", 0));
    }

    public static API getInstance() {
        API localInstance = Instance;
        if (localInstance == null) {
            synchronized (API.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new API();
                }
            }
        }
        return localInstance;
    }

}
