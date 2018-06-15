package team.circleofcampus.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaye Li on 2018/6/15.
 */

public class SocietyAuthorityRequest {

    /**
     * 提交权限申请请求
     * @param uId - 用户ID
     * @param sId - 社团ID
     * @param reason - 申请原因
     * @return
     */
    public static String submitSocietyAuthorityRequest(int uId, int sId, String reason) {
        try {
            JSONObject json = new JSONObject();
            json.put("uId", uId);
            json.put("sId", sId);
            json.put("reason", reason);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/addSocietyCircleRequest.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取此用户所在学校的所有社团信息
     * @param uId - 用户ID
     * @return
     */
    public static String getAllSociety(int uId) {
        try {
            JSONObject json = new JSONObject();
            json.put("uId", uId);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/getAllSociety.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
