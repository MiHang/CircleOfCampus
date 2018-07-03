package team.circleofcampus.dao;

import android.content.Context;

import com.common.model.Message;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import team.circleofcampus.db.DatabaseHelper;

/**
 * Created by 惠普 on 2018-07-03.
 */

public class Data_Dao {
    DatabaseHelper helper;
    Dao<Message, Integer> message;

    public Data_Dao(Context context) throws SQLException {
        this.helper = new DatabaseHelper(context);
        this.message = this.helper.getDao(Message.class);


    }

    public List<Message> getMessage(String Send, String Receive) {
        List<Message> data = new ArrayList();

        try {
            data = message.queryBuilder().where().in("Send",Send, Receive).and()
                    .in("Receive", Send, Receive).query();
        } catch (SQLException var9) {
            var9.printStackTrace();
        }

        return data;
    }

    public void save(Message dataMsg) throws SQLException {
            message.create(dataMsg);
    }

    public void update(Message dataMsg) throws SQLException {
        message.update(dataMsg);
    }
    public Message getMsgById(int id) {
        Message dataMsg = null;
        try {
           dataMsg=message.queryForId(id);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }
return dataMsg;
    }
    public List<Message> getAllMsg() {
       List< Message> dataMsg = null;
        try {
            dataMsg=message.queryForAll();
        } catch (SQLException var3) {
            var3.printStackTrace();
        }
        return dataMsg;
    }
}
