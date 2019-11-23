package com.jadebyte.jadeplayer.main.shazam.api;

import com.jadebyte.jadeplayer.main.shazam.model.Artist;
import com.jadebyte.jadeplayer.main.shazam.model.Metadata;
import com.jadebyte.jadeplayer.main.shazam.model.Music;
import com.jadebyte.jadeplayer.main.shazam.model.ResultTrack;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AcrCloudApi {

    private static final String accessKey = "405f4799b4778532f1fa06347e3c8fd3";
    private static final String host = "identify-eu-west-1.acrcloud.com";
    private static final String accessSecret = "JqvVESxnwnH4hFn7V2g6P4fpBPskJ6s4yRJRvL95";

    public ResultTrack recognizeVoice(File file, boolean humming) {
        try {
            byte[] queryData = FileUtils.readFileToByteArray(file);
            String resultString = _recognize(host, accessKey, accessSecret, queryData, "audio", 10000);
            if (resultString == null || resultString.isEmpty()) return null;

            JSONObject main = new JSONObject(resultString);
            JSONObject metadata = main.getJSONObject("metadata");
            JSONArray musicArray = metadata.getJSONArray("music");
            if (musicArray.length() == 0) return null;
            JSONObject music = musicArray.getJSONObject(0);
            String album = music.getJSONObject("album").getString("name");
            String title = music.getString("title");
            StringBuilder artist = new StringBuilder();
            JSONArray artists = music.getJSONArray("artists");
            for (int i = 0; i < artists.length(); i++) {
                artist.append(artists.getJSONObject(i).getString("name")).append(" ");
            }
            return new ResultTrack(title, artist.toString(), album);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String _recognize(String host, String accessKey, String secretKey, byte[] queryData, String queryType, int timeout) {
        String method = "POST";
        String httpURL = "/v1/identify";
        String dataType = queryType;
        String sigVersion = "1";
        String timestamp = getUTCTimeSeconds();

        String reqURL = "http://" + host + httpURL;

        String sigStr = method + "\n" + httpURL + "\n" + accessKey + "\n" + dataType + "\n" + sigVersion + "\n" + timestamp;
        String signature = encryptByHMACSHA1(sigStr.getBytes(), secretKey.getBytes());

        Map<String, Object> postParams = new HashMap<String, Object>();
        postParams.put("access_key", accessKey);
        postParams.put("sample_bytes", queryData.length + "");
        postParams.put("sample", queryData);
        postParams.put("timestamp", timestamp);
        postParams.put("signature", signature);
        postParams.put("data_type", queryType);
        postParams.put("signature_version", sigVersion);

        String res = postHttp(reqURL, postParams, timeout);

        return res;
    }

    private String encodeBase64(byte[] bstr) {
        Base64 base64 = new Base64();
        return new String(base64.encode(bstr));
    }

    private String postHttp(String posturl, Map<String, Object> params, int timeOut) {
        String res = "";
        String BOUNDARYSTR = "*****2015.03.30.acrcloud.rec.copyright." + System.currentTimeMillis() + "*****";
        String BOUNDARY = "--" + BOUNDARYSTR + "\r\n";
        String ENDBOUNDARY = "--" + BOUNDARYSTR + "--\r\n\r\n";

        String stringKeyHeader = BOUNDARY +
                "Content-Disposition: form-data; name=\"%s\"" +
                "\r\n\r\n%s\r\n";
        String filePartHeader = BOUNDARY +
                "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n";

        URL url = null;
        HttpURLConnection conn = null;
        BufferedOutputStream out = null;
        BufferedReader reader = null;
        ByteArrayOutputStream postBufferStream = new ByteArrayOutputStream();
        try {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value instanceof String || value instanceof Integer) {
                    postBufferStream.write(String.format(stringKeyHeader, key, (String) value).getBytes());
                } else if (value instanceof byte[]) {
                    postBufferStream.write(String.format(filePartHeader, key, key).getBytes());
                    postBufferStream.write((byte[]) value);
                    postBufferStream.write("\r\n".getBytes());
                }
            }
            postBufferStream.write(ENDBOUNDARY.getBytes());

            url = new URL(posturl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(timeOut);
            conn.setReadTimeout(timeOut);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-type", "multipart/form-data;boundary=" + BOUNDARYSTR);

            conn.connect();
            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(postBufferStream.toByteArray());
            out.flush();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String tmpRes = "";
                while ((tmpRes = reader.readLine()) != null) {
                    if (tmpRes.length() > 0)
                        res = res + tmpRes;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (postBufferStream != null) {
                    postBufferStream.close();
                    postBufferStream = null;
                }
                if (out != null) {
                    out.close();
                    out = null;
                }
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private String encryptByHMACSHA1(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data);
            return encodeBase64(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getUTCTimeSeconds() {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return cal.getTimeInMillis() / 1000 + "";
    }
}