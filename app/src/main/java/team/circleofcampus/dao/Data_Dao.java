package team.circleofcampus.dao;

/**
 * Created by 惠普 on 2018-07-02.
 */


import android.content.Context;

import com.common.model.Message;
import com.common.model.Msg;
import com.common.model.UserMsg;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import team.circleofcampus.db.DatabaseHelper;

public class Data_Dao {
    DatabaseHelper helper;

    Dao<Message, Integer> message;
    Dao<UserMsg,Integer> userMsg_dao;

    public Data_Dao(Context context) throws SQLException {
        helper = new DatabaseHelper(context);
            message = helper.getDao(Message.class);

            userMsg_dao=helper.getDao(UserMsg.class);



    }


    public List<Message> getAllMessage() throws SQLException {
        List<Message> data = new ArrayList();
        data = this.message.queryForAll();
        return data;

    }

    public List<Message> getMessage(String Send, String Receive) throws SQLException {
        ArrayList data = new ArrayList();


            QueryBuilder queryBuilder = message.queryBuilder();
            queryBuilder.where().eq("Send", Send).or().eq("Receive", Send).and().eq("Receive", Receive).or().eq("Send", Receive);
            List<Msg> msg = queryBuilder.query();
            Iterator var6 = msg.iterator();

            while(var6.hasNext()) {
                Msg m = (Msg)var6.next();
                List<Message> Msg = message.queryForEq("Id", Integer.valueOf(m.getId()));
                data.add(Msg.get(0));
            }

        return data;
    }

    public void setData(Message dataMsg) {
        try {
            message.create(dataMsg);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public Message queryMsgById(int id) {
        Message data = null;

        try {
            data = message.queryForId(Integer.valueOf(id));
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

        return data;
    }

    public void update(Message dataMsg) {
        try {
            message.update(dataMsg);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public Message queryMessageById(int id) {
        Message data = null;

        try {
            data =message.queryForId(Integer.valueOf(id));
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

        return data;
    }




}
