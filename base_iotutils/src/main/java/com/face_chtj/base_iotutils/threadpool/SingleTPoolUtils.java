package com.face_chtj.base_iotutils.threadpool;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 单线程执行 队列线程池
 * 无返回值线程执行 {@link #execute(Runnable runnable)}
 * 有返回值线程执行 {@link #submit(Callable callable)}
 * 查看任务是否结束 {@link #isTaskEnd()}
 * 关闭线程池 {@link #shutdown()}
 * 关闭线程池 {@link #shutdownNow()}
 *
 * @author chtj
 */
public class SingleTPoolUtils {
    private static final String TAG = "SingleTPoolUtils";

    private static ThreadPoolExecutor mThreadPool;

    /*线程池维护线程的最少数量 核心线程数*/
    private static final int SIZE_CORE_POOL = 1;

    /*线程池维护线程的最大数量 最大线程数*/
    private static final int SIZE_MAX_POOL = 1;

    /*线程池维护线程所允许的空闲时间*/
    private static final int TIME_KEEP_ALIVE = 5000;

    /*线程池所使用的缓冲队列大小*/
    private static final int SIZE_WORK_QUEUE = 500;

    /**
     * 线程池单例创建方法
     */
    private static ThreadPoolExecutor newInstance() {
        if (mThreadPool != null) {
            return mThreadPool;
        } else {
            synchronized (SingleTPoolUtils.class) {
                if (mThreadPool == null) {
                    mThreadPool = new ThreadPoolExecutor(SIZE_CORE_POOL, SIZE_MAX_POOL,
                            TIME_KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(SIZE_WORK_QUEUE), new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
                            Log.d(TAG, "rejectedExecution: task=" + task.toString() + ",rejected from=" + executor.toString());
                        }
                    });
                }
                return mThreadPool;
            }
        }
    }

    /**
     * 将构造方法访问修饰符设为私有，禁止任意实例化。
     */
    private SingleTPoolUtils() {
    }

    /**
     * 无返回值直接执行
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        newInstance().execute(runnable);
    }

    /**
     * 返回值直接执行
     *
     * @param callable
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return newInstance().submit(callable);
    }


    /**
     * 查看任务是否结束
     *
     * @return
     */
    public static boolean isTaskEnd() {
        if(mThreadPool==null){
            return true;
        }else{
            if(mThreadPool.getActiveCount() == 0){
                return true;
            }else{
                return false;
            }
        }
    }

    /**
     * 当线程池调用该方法时,线程池的状态则立刻变成SHUTDOWN状态。
     * 此时，则不能再往线程池中添加任何任务，否则将会抛出RejectedExecutionException异常。
     * 但是，此时线程池不会立刻退出，直到添加到线程池中的任务都已经处理完成，才会退出
     */
    public static void shutdown() {
        newInstance().shutdown();
        mThreadPool = null;
    }

    /**
     * 执行该方法，线程池的状态立刻变成STOP状态，并试图停止所有正在执行的线程，不再处理还在池队列中等待的任务，当然，它会返回那些未执行的任务
     * 它试图终止线程的方法是通过调用Thread.interrupt()方法来实现的，但是大家知道，这种方法的作用有限，如果线程中没有sleep 、wait、Condition、定时锁等应用, interrupt()方法是无法中断当前的线程的
     * 并不代表线程池就一定立即就能退出，它可能必须要等待所有正在执行的任务都执行完成了才能退出。
     */
    public static void shutdownNow() {
        newInstance().shutdownNow();
        mThreadPool = null;
    }
}
