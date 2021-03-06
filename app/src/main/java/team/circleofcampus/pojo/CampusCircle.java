package team.circleofcampus.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 本地数据库校园圈表映射对象
 */
@DatabaseTable(tableName = "t_camus_circle")
public class CampusCircle implements Serializable {
    /**
     * ID
     * 禁止自增长主键
     */
    @DatabaseField(columnName = "id", generatedId = false)
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
    private String imagesUrl;
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

    public CampusCircle() {}

    public CampusCircle(int id, String title,
                        String content, String imagesUrl,
                        String publishTime, String venue,
                        String activityTime) {
        this.id = id;
        this.title = title;
        this.content = content;
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
}
