package team.circleofcampus.http;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
     * 查询是否有用户请求添加好友
     * @param account
     * @return
     */
    public String QueryRequestAddFriendInfo(String account) {
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
                .url(url+"coc/QueryRequestAddFriendInfo.do")
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
     * 查询两人是否是好友
     * @param user1 ,user2
     * @return
     */
    public String QueryIsFriend(String user1,String user2) {
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject js = new JSONObject();

        try {
            js.put("user1", user1);
            js.put("user2", user2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf8");
        RequestBody requestBody = RequestBody.create(mediaType, js.toString());
        Request request = new Request.Builder()
                .url(url+"coc/queryIsFriend.do")
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
                .url(url+"/coc/unclearSearch.do")
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

    /**
     * 请求添加好友
     * @param user1 用户1
     * @param user2 用户2
     * @param reason 申请理由
     * @return
     */
    public String requestAddFriend(String user1,String user2,String reason) {
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject js = new JSONObject();

        try {
            js.put("user1", user1);
            js.put("user2", user2);
            js.put("reason", reason);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf8");
        RequestBody requestBody = RequestBody.create(mediaType, js.toString());
        Request request = new Request.Builder()
                .url(url+"/coc/requestAddFriend.do")
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return    response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * 通过账号获取好友信息
     * @param account
     * @return
     */
    public String queryFriendInfo(String account) {
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
                .url(url+"coc/queryFriendInfo.do")
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().string();
            }else{
                return response.code()+"";
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
