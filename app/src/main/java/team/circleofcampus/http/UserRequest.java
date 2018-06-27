package team.circleofcampus.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaye Li on 2018/6/27.
 */

public class UserRequest {
    /**
     * 忘记密码
     * @param uId
     * @param username
     * @param gender
     * @return
     */
    public static String updateUserInfo(int uId, String username, String gender) {
        try {
            JSONObject json = new JSONObject();
            json.put("uId", uId);
            json.put("userName", username);
            json.put("gender", gender);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/updateUserInfo.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
