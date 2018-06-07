package team.circleofcampus.model;

public class SocietyCircle extends Circle {

    // 审核状态，1 - 已审核，0 - 未审核
    private int auditing;
    // 用户ID
    private int userId;

    public SocietyCircle() {}

    public SocietyCircle(int id, String title,
                         String content, String imagesUrl,
                         String publishTime, String venue,
                         String activityTime,
                         int auditing, int userId) {
        super(id, title, content, imagesUrl, publishTime, venue, activityTime);
        this.auditing = auditing;
        this.userId = userId;
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
