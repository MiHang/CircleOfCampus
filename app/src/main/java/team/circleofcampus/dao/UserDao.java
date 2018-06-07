package team.circleofcampus.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import team.circleofcampus.db.DatabaseHelper;
import team.circleofcampus.pojo.User;


/**
 * 用户表数据库操作类
 */
public class UserDao {

    private DatabaseHelper helper;

    /**
     * 数据库操作对象
     * 其中 Dao<User, Integer> 中的User表示操作的表为用户表，
     *  Integer表示此表的主键为int类型
     */
    private Dao<User, Integer> dao;

    public UserDao(Context context) throws SQLException {
        helper = new DatabaseHelper(context);
        dao = helper.getDao(User.class);
    }

    /**
     * 向用户表插入多行数据
     * @param users - Collection<User>
     * @return 1 - 数据插入成功， 0 - 数据插入失败
     */
    public int insertData(Collection<User> users) {
        try {
            dao.create(users);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 向用户表插入一行数据
     * @param user - User
     * @return 1 - 数据插入成功， 0 - 数据插入失败
     */
    public int insertData(User user) {
        try {
            dao.create(user);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除多行数据
     * @param users
     * @return 1 - 数据删除成功， 0 - 数据删除失败
     */
    public int deleteData(Collection<User> users) {
        try {
            dao.delete(users);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除一行数据
     * @param user
     * @return 1 - 数据删除成功， 0 - 数据删除失败
     */
    public int deleteData(User user) {
        try {
            dao.delete(user);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新一行数据
     * @param user
     * @return 1 - 数据更新成功， 0 - 数据更新失败
     */
    public int updateData(User user) {
        try {
            dao.update(user);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 通过用户ID查询数据
     * @param u_id - 用户ID
     * @return 查询成功返回User，失败返回null
     */
    public User queryData(int u_id) {
        try {
            return dao.queryForId(u_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询用户表所有数据
     * @return 查询成功返回 List<User>， 失败返回null
     */
    public List<User> queryData() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
