package team.circleofcampus.Interface;

/**
 * 网络连接状态改变监听
 */
public interface NetworkStateChangeListener {
    /**
     * 网络连接可用
     * @param type - 网络连接类型
     */
    void networkAvailable(int type);
    /**
     * 网络连接不可用
     */
    void networkUnavailable();
}
