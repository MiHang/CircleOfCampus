package team.circleofcampus.http;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * 校园圈网络连接类
 */
public class CampusCircleRequest {

    /**
     * 获取用户所在学校的校园圈记录的大小
     * @param uId - 用户ID
     * @return
     */
    public static String getCampusCircleSize(int uId) {
        try {
            JSONObject json = new JSONObject();
            json.put("uId", uId);
            return HttpRequest.postRequest(HttpRequest.URL + "coc/getCampusCircleSize.do", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
