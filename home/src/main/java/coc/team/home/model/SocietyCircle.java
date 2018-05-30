package coc.team.home.model;

import java.io.Serializable;

public class SocietyCircle implements Serializable {

    // 社团圈ID
    private int id;
     // 标题
    private String title;
    // 文本内容
    private String content;
    // 图片地址
    private String imagesUrl;
    // 发布时间
    private String publishTime;
    // 活动地点
    private String venue;
    // 活动时间
    private String activityTime;
    // 审核状态，1 - 已审核，0 - 未审核
    private int auditing;
    // 用户ID
    private int userId;

    public SocietyCircle() {}

    public SocietyCircle(int id, String title,
                         String content, String imagesUrl,
                         String publishTime, String venue,
                         String activityTime, int auditing,
                         int userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imagesUrl = imagesUrl;
        this.publishTime = publishTime;
        this.venue = venue;
        this.activityTime = activityTime;
        this.auditing = auditing;
        this.userId = userId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getAuditing() {
        return auditing;
    }

    public void setAuditing(int auditing) {
        this.auditing = auditing;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
