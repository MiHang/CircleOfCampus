package team.circleofcampus.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import team.circleofcampus.db.DatabaseHelper;
import team.circleofcampus.pojo.MyPublishSocietyCircle;
import team.circleofcampus.pojo.SocietyCircle;

/**
 * 我发布的社团公告表数据库操作类
 */
public class MyPublishSocietyCircleDao {

    private DatabaseHelper helper;

    /**
     * 数据库操作对象
     * 其中 Dao<MyPublishSocietyCircle, Integer> 中的User表示操作的表为用户表，
     *  Integer表示此表的主键为int类型
     */
    private Dao<MyPublishSocietyCircle, Integer> dao;

    public MyPublishSocietyCircleDao(Context context) throws SQLException {
        helper = new DatabaseHelper(context);
        dao = helper.getDao(MyPublishSocietyCircle.class);
    }

    /**
     * 向用户表插入多行数据
     * @param societyCircles - Collection<MyPublishSocietyCircle>
     * @return 1 - 数据插入成功， 0 - 数据插入失败
     */
    public int insertData(Collection<MyPublishSocietyCircle> societyCircles) {
        try {
            dao.create(societyCircles);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 向用户表插入一行数据
     * @param societyCircle
     * @return 1 - 数据插入成功， 0 - 数据插入失败
     */
    public int insertData(MyPublishSocietyCircle societyCircle) {
        try {
            dao.create(societyCircle);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除全部数据
     * @return 1 - 数据删除成功， 0 - 数据删除失败
     */
    public int deleteForAllData() {
        try {
            List<MyPublishSocietyCircle> societyCircles = dao.queryForAll();
            dao.delete(societyCircles);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除多行数据
     * @param societyCircles
     * @return 1 - 数据删除成功， 0 - 数据删除失败
     */
    public int deleteData(Collection<MyPublishSocietyCircle> societyCircles) {
        try {
            dao.delete(societyCircles);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除一行数据
     * @param societyCircle
     * @return 1 - 数据删除成功， 0 - 数据删除失败
     */
    public int deleteData(MyPublishSocietyCircle societyCircle) {
        try {
            dao.delete(societyCircle);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新一行数据
     * @param societyCircle
     * @return 1 - 数据更新成功， 0 - 数据更新失败
     */
    public int updateData(MyPublishSocietyCircle societyCircle) {
        try {
            dao.update(societyCircle);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询前num条数据
     * @return 查询成功返回SocietyCircle，失败返回null
     */
    public List<MyPublishSocietyCircle> queryDataTopNum(long num) {
        try {
            List<MyPublishSocietyCircle> societyCircles = dao.queryBuilder().limit(num).query();
            if (societyCircles != null && societyCircles.size() > 0) {
                return societyCircles;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过用户ID查询数据
     * @param sid - 社团圈ID
     * @return 查询成功返回SocietyCircle，失败返回null
     */
    public MyPublishSocietyCircle queryDataById(int sid) {
        try {
            List<MyPublishSocietyCircle> societyCircles = dao.queryBuilder().where().eq("id", sid).query();
            if (societyCircles != null && societyCircles.size() > 0) {
                return societyCircles.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询用户表所有数据
     * @return 查询成功返回 List<SocietyCircle>， 失败返回null
     */
    public List<MyPublishSocietyCircle> queryData() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
