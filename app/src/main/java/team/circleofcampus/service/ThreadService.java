package team.circleofcampus.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池服务
 */
public class ThreadService {

    /**
     * 获取一个单例的线程池
     * @return
     */
    public static ExecutorService getSingleThreadPool(){
        return Executors.newSingleThreadExecutor();
    }
}
