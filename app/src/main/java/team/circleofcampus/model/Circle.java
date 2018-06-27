package team.circleofcampus.model;


import java.io.Serializable;

/**
 * 校园圈与社团圈共有的信息
 */
public class Circle implements Serializable {

    // 圈ID, id 为-1，则此对象为时间戳
    private int id;
    // 标题
    private String title;
    // 图片地址
    private String imagesUrl;
    // 发布时间
    private String publishTime;
    // 活动地点
    private String venue;
    // 活动时间
    private String activityTime;

    public Circle() {}

    public Circle(int id, String title,
                  String imagesUrl, String publishTime,
                  String venue, String activityTime) {
        this.id = id;
        this.title = title;
        this.imagesUrl = imagesUrl;
        this.publishTime = publishTime;
        this.venue = venue;
        this.activityTime = activityTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }
}
