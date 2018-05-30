package coc.team.home.http;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import coc.team.home.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 惠普 on 2018-05-16.
 */

public class HttpHelper {
    String url="http://192.168.1.157:8080/";
    Context context;
    public HttpHelper(Context context){
        this.context=context;
    }
    public String getPath(){
        return url;
    }

    /**
     * 通过账号获取用户信息
     * @param account
     * @return
     */
    public String getUserInfoByAccount(String account) {
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject js = new JSONObject();

        try {
            js.put("account", account);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf8");
        RequestBody requestBody = RequestBody.create(mediaType, js.toString());
        Request request = new Request.Builder()
                .url(url+"coc/getUserInfoByAccount.do")
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 通过账号获取用户信息
     * @param account
     * @return
     */
    public String getUserInfoBySearch(String account) {
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject js = new JSONObject();

        try {
            js.put("Search", account);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf8");
        RequestBody requestBody = RequestBody.create(mediaType, js.toString());
        Request request = new Request.Builder()
                .url(url+"coc/getUserInfoBy.do")
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取用户名与性别
     * @param Account
     * @return
     */
    public String getUserNameAndSex(String Account){
        OkHttpClient okHttpClient=new OkHttpClient();
        MediaType mediaType=MediaType.parse("application/json;charset=utf8");
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("Account" ,Account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(mediaType,jsonObject.toString());
        Request request=new Request.Builder().url(url+"/coc/UserNameAndSexQuery.do").post(requestBody).build();
        try {
            Response response=okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
