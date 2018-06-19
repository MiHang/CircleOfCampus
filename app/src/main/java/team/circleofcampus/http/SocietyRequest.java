package team.circleofcampus.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 社团圈网络连接类
 */
public class SocietyRequest {

    /**
     *
     * @param uId
     * @param title
     * @param content
     * @param venue
     * @param activityTime
     * @param image
     * @return
     */
    public static String addSocietyCircle(int uId, String title,
                                          String content, String venue,
                                          String activityTime, File image) {
        try {
            JSONObject json = new JSONObject();
            json.put("uId", uId);
            json.put("title", title);
            json.put("content", content);
            json.put("venue", venue);
            json.put("activityTime", activityTime);
            return HttpRequest.uploadImageAndParam(HttpRequest.URL + "coc/addSocietyCircle.do", json.toString(), image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
