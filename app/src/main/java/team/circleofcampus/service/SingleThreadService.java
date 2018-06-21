package team.circleofcampus.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单例线程池服务
 */
public class SingleThreadService {

    private static ExecutorService singleThreadExecutor;

    /**
     * 获取一个单例的线程池
     * @return
     */
    public static ExecutorService getSingleThreadPool(){
        if (singleThreadExecutor == null) {
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        return singleThreadExecutor;
    }
}
