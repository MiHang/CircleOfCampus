package team.circleofcampus.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jaye Li on 2018/6/2.
 */

public class HttpRequest {

    private static final MediaType mediatype = MediaType.parse("application/json;charset=utf-8");

    /**
     * okhttp请求
     * @param url - 请求地址
     * @param param - 请求参数
     * @return 请求结果, 失败返回null
     */
    public static String postRequest(String url, String param) throws IOException {

        RequestBody requestBody = RequestBody.create(mediatype, param);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            try {
                throw new Exception("request error : error code = " + response.code());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
