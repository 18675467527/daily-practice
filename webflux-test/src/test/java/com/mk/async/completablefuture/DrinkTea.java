package com.mk.async.completablefuture;

import com.mk.async.threadpool.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @Author hjm
 * @Date 2024/11/19 14:49
 * 代码模拟泡茶全过程
 * 使用了异步任务编排
 * 主要为了展示任务的编排，以及任务之间的依赖关系
 */

public class DrinkTea {
    private static final Logger logger = LoggerFactory.getLogger(DrinkTea.class);

    public static void main(String[] args) {

        //ExecutorService drinkTeaPool = ThreadManager.getPool("DrinkTeaPool-");
        ExecutorService drinkTeaPool = ThreadPoolUtil.getThreadPool("DrinkTeaPool");

        // 任务1 洗杯子
        CompletableFuture<Boolean> washJob =
                CompletableFuture.supplyAsync(() ->
                {
                    doWork("洗杯子", 3000, false);
                    return true;
                }, drinkTeaPool).handle((res, ex) -> {
                    if (ex != null) {
                        logger.error("杯子碎了");
                        return false;
                    }
                    return res;
                });

        // 任务2 烧水
        CompletableFuture<Boolean> hotJob =
                CompletableFuture.supplyAsync(() ->
                {
                    doWork("烧水", 5000, true);
                    return true;
                }, drinkTeaPool).handle((res, ex) -> {
                    if (ex != null) {
                        logger.error("煤气用完了");
                        return false;
                    }
                    return res;
                });

        // 任务3 泡茶：任务1和任务2都完成后执行 任务1和任务2有失败的话，不会执行
        CompletableFuture<Boolean> drinkJob =
                washJob.thenCombineAsync(hotJob, (washOK, hotOk) ->
                {
                    if (hotOk && washOK) {
                        // 执行泡茶任务
                        doWork("泡茶", 2000, false);
                        return true;
                    } else if (!hotOk && washOK) {
                        logger.error("因为煤气用完了，没有泡茶");
                        return false;
                    } else if (hotOk && !washOK) {
                        logger.error("因为杯子碎了，没有泡茶");
                        return false;
                    } else {
                        logger.error("杯子碎了，煤气也用完了，没有泡茶");
                        return false;
                    }
                }, drinkTeaPool).handle((res, ex) -> {
                    if (ex != null) {
                        logger.error("泡茶的时候烫到手了");
                        return false;
                    }
                    return res;
                });

        logger.info("主程序开始执行，等待泡茶任务执行完毕");

        ThreadPoolUtil.submitAndWait("DrinkTeaPool", () -> {
            if (drinkJob.join()) {
                logger.info("烧水，洗杯子，泡茶均执行完毕");
                logger.info("可以喝茶了");
            } else {
                logger.error("没有喝到茶");
            }
        });

        logger.info("主程序结束执行，关闭线程池，退出主程序");
        drinkTeaPool.shutdown();
    }

    /**
     * 模拟执行一项工作，包括开始、工作进行中和结束的流程
     * 此方法主要用于打印开始信息、模拟一段时间的工作以及打印结束信息
     *
     * @param projectName 项目的名称
     * @param workTime    模拟工作所需要的时间，单位为毫秒
     * @param hasError    模拟工作是否出错
     */
    private static void doWork(String projectName, int workTime, Boolean hasError) {
        // 打印工作开始的信息
        logger.info("开始" + projectName);
        // 线程睡眠一段时间，代表工作中
        try {
            Thread.sleep(workTime);
            // 模拟工作出错的情况
            if (hasError) {
                throw new Exception(projectName + "出错了");
            }
        } catch (Exception e) {
            // 如果工作出错，抛出运行时异常
            throw new RuntimeException(projectName + "出错了");
        }
        // 打印工作结束的信息
        logger.info(projectName + "完成");
    }
}


