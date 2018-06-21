package team.circleofcampus.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 社团圈网络连接类
 */
public class SocietyCircleRequest {

    /**
     * 查询某用户发布的社团圈信息
     * @return
     */
    public static String getMyPublishSocietyCircle() {
        return null;
    }

    /**
     * 获取社团圈数量
     * @param uId - 用户ID
     * @return
     */
    public static String getSocietyCircleSize(int uId) {
        try {
            JSONObject json = new JSONObject();
            json.put("uId", uId);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/getSocietyCircleSize.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户发布的社团圈数量
     * @param uId - 用户ID
     * @return
     */
    public static String getMyPublishSocietyCircleSize(int uId) {
        try {
            JSONObject json = new JSONObject();
            json.put("uId", uId);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/getMyPublishSocietyCircleSize.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
