package team.circleofcampus.Interface;


import team.circleofcampus.model.UserMsg;

/**
 * Created by 惠普 on 2018-05-15.
 */

public interface MsgBroadcastReceiverListener {
    void sendMessageListener(UserMsg userMsg);
}
