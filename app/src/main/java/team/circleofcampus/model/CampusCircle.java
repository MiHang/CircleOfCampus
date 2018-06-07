package team.circleofcampus.model;

/**
 * Created by Jaye Li on 2018/6/7.
 */

public class CampusCircle extends Circle {
    int cId; // 校园ID

    public CampusCircle() {}

    public CampusCircle(int id, String title,
                        String content, String imagesUrl,
                        String publishTime, String venue,
                        String activityTime, int cId) {
        super(id, title, content, imagesUrl, publishTime, venue, activityTime);
        this.cId = cId;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }
}
