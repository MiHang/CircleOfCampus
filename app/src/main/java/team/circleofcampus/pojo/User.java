package team.circleofcampus.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 本地数据库用户表映射对象
 */
@DatabaseTable(tableName = "t_user")
public class User implements Serializable {
    /**
     * 用户ID
     * 禁止自增长主键
     */
    @DatabaseField(columnName = "id", generatedId = false)
    private int uId;
    /**
     * 用户名
     */
    @DatabaseField(columnName = "name")
    private String userName;
    /**
     * 用户头像
     */
    @DatabaseField(columnName = "head_icon")
    private String headIcon;
    /**
     * 电子邮箱
     */
    @DatabaseField(columnName = "email")
    private String email;
    /**
     * 性别
     */
    @DatabaseField(columnName = "gender")
    private String gender;
    /**
     * 学校名
     */
    @DatabaseField(columnName = "campus_name")
    private String campusName;
    /**
     * 院系名
     */
    @DatabaseField(columnName = "faculty_name")
    private String facultyName;

    public User() {}

    public User(int uId, String userName,
                String headIcon, String email,
                String gender, String campusName,
                String facultyName) {
        this.uId = uId;
        this.userName = userName;
        this.headIcon = headIcon;
        this.email = email;
        this.gender = gender;
        this.campusName = campusName;
        this.facultyName = facultyName;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}
