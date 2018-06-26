package team.circleofcampus.model;

import java.io.Serializable;

/**
 * 公告详情model
 * @author Jaye Li
 */
public class CircleDetail implements Serializable {

    private String publisher;
    private String publisherIco;
    private String publishTime;
    private String title;
    private String content;
    private String imagesUrl;

    public CircleDetail() {}

    public CircleDetail(String publisher, String publisherIco,
                        String publishTime, String title,
                        String content, String imagesUrl) {
        this.publisher = publisher;
        this.publisherIco = publisherIco;
        this.publishTime = publishTime;
        this.title = title;
        this.content = content;
        this.imagesUrl = imagesUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherIco() {
        return publisherIco;
    }

    public void setPublisherIco(String publisherIco) {
        this.publisherIco = publisherIco;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
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
}
