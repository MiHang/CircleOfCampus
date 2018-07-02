package team.circleofcampus.Interface;


import com.common.model.UserMsg;

/**
 * Created by 惠普 on 2018-05-15.
 */
public interface  MessageListener{
    void update(UserMsg msg, boolean isUpdate);

}
