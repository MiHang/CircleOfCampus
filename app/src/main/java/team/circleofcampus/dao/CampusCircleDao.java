package team.circleofcampus.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import team.circleofcampus.db.DatabaseHelper;
import team.circleofcampus.pojo.CampusCircle;
import team.circleofcampus.pojo.User;

/**
 * 用户表数据库操作类
 */
public class CampusCircleDao {

    private DatabaseHelper helper;

    /**
     * 数据库操作对象
     * 其中 Dao<User, Integer> 中的User表示操作的表为用户表，
     *  Integer表示此表的主键为int类型
     */
    private Dao<CampusCircle, Integer> dao;

    public CampusCircleDao(Context context) throws SQLException {
        helper = new DatabaseHelper(context);
        dao = helper.getDao(CampusCircle.class);
    }

    /**
     * 向用户表插入多行数据
     * @param campusCircles - Collection<CampusCircle>
     * @return 1 - 数据插入成功， 0 - 数据插入失败
     */
    public int insertData(Collection<CampusCircle> campusCircles) {
        try {
            dao.create(campusCircles);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 向用户表插入一行数据
     * @param campusCircle - User
     * @return 1 - 数据插入成功， 0 - 数据插入失败
     */
    public int insertData(CampusCircle campusCircle) {
        try {
            dao.create(campusCircle);
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
            List<CampusCircle> campusCircles = dao.queryForAll();
            dao.delete(campusCircles);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除多行数据
     * @param campusCircles
     * @return 1 - 数据删除成功， 0 - 数据删除失败
     */
    public int deleteData(Collection<CampusCircle> campusCircles) {
        try {
            dao.delete(campusCircles);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除一行数据
     * @param campusCircle
     * @return 1 - 数据删除成功， 0 - 数据删除失败
     */
    public int deleteData(CampusCircle campusCircle) {
        try {
            dao.delete(campusCircle);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新一行数据
     * @param campusCircle
     * @return 1 - 数据更新成功， 0 - 数据更新失败
     */
    public int updateData(CampusCircle campusCircle) {
        try {
            dao.update(campusCircle);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询前num条数据
     * @return 查询成功返回CampusCircle，失败返回null
     */
    public List<CampusCircle> queryDataTopNum(long num) {
        try {
            List<CampusCircle> campusCircles = dao.queryBuilder().limit(num).query();
            if (campusCircles != null && campusCircles.size() > 0) {
                return campusCircles;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过用户ID查询数据
     * @param cid - 校园圈ID
     * @return 查询成功返回CampusCircle，失败返回null
     */
    public CampusCircle queryDataById(int cid) {
        try {
            List<CampusCircle> campusCircles = dao.queryBuilder().where().eq("id", cid).query();
            if (campusCircles != null && campusCircles.size() > 0) {
                return campusCircles.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询用户表所有数据
     * @return 查询成功返回 List<User>， 失败返回null
     */
    public List<CampusCircle> queryData() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
