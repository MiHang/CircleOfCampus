package team.circleofcampus.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 本地数据库社团圈表映射对象
 */
@DatabaseTable(tableName = "t_society_circle")
public class SocietyCircle implements Serializable {
    /**
     * ID
     * 自增长主键
     */
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    /**
     * 标题
     */
    @DatabaseField(columnName = "title")
    private String title;
    /**
     * 内容
     */
    @DatabaseField(columnName = "content")
    private String content;
    /**
     * 图片地址，多个图片使用json数组表示
     */
    @DatabaseField(columnName = "images_url")
    private String imagesUrls;
    /**
     * 发布时间
     */
    @DatabaseField(columnName = "publish_time")
    private String publishTime;
    /**
     * 活动地点
     */
    @DatabaseField(columnName = "venue")
    private String venue;
    /**
     * 活动时间
     */
    @DatabaseField(columnName = "activity_time")
    private String activityTime;
    /**
     * 审核状态，1 - 已审核，0 - 未审核
     */
    @DatabaseField(columnName = "auditing")
    private int auditing;

    public SocietyCircle() {}

    public SocietyCircle(int id, String title,
                         String content, String imagesUrls,
                         String publishTime, String venue,
                         String activityTime, int auditing) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imagesUrls = imagesUrls;
        this.publishTime = publishTime;
        this.venue = venue;
        this.activityTime = activityTime;
        this.auditing = auditing;
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

    public String getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(String imagesUrls) {
        this.imagesUrls = imagesUrls;
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
}
