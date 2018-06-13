package team.circleofcampus.http;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jaye Li on 2018/6/2.
 */
public class HttpRequest {
    public static final String IP = "192.168.1.157";
    public static final String URL = "http://"+IP+":8080/";
    private static final MediaType mediatype = MediaType.parse("application/json;charset=utf-8");

    /**
     * okhttp请求
     * @param url
     * @param param
     */
    public static String postRequest(String url, String param) {

        RequestBody requestBody = RequestBody.create(mediatype, param);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                Log.e("coc error", "request failed, error code = " + response.code());
                throw new Exception("request failed, error code = " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
