package team.circleofcampus.dao;

import android.content.Context;

import com.common.model.Message;
import com.common.model.UserMsg;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import team.circleofcampus.db.DatabaseHelper;

/**
 * Created by 惠普 on 2018-07-03.
 */

public class UserMsg_Dao {
    DatabaseHelper helper;
    Dao<UserMsg,Integer> userMsg_dao;

    public UserMsg_Dao(Context context) throws SQLException {
        helper = new DatabaseHelper(context);
        userMsg_dao=helper.getDao(UserMsg.class);
    }
    public List<UserMsg> getAllMsg(){
        List<UserMsg> data=new ArrayList<>();
        try {

            data=userMsg_dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    public void add(UserMsg msg){
        try {
            userMsg_dao.createOrUpdate(msg);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update(UserMsg msg){
        try {
            userMsg_dao.update(msg);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * 查询是否存在
     * @param
     * @return
     */
    public List<UserMsg> queryMsgBySearch(String search) {
        List<UserMsg> data = new ArrayList<>();
        try {
            QueryBuilder<UserMsg,Integer> queryBuilder=userMsg_dao.queryBuilder();
            data =  queryBuilder.where().eq("Account",search).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (data != null && data.size() > 0) return data;
        return null;
    }
    /**
     * 根据账号删除
     * @param search
     * @return
     */
    public void deleteByAccount(String search) {

        try {
            DeleteBuilder<UserMsg,Integer> deleteBuilder=userMsg_dao.deleteBuilder();
            deleteBuilder.where().eq("Account",search);
            deleteBuilder.delete();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
