package com.alibaba.csp.sentinel.init;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.alibaba.csp.sentinel.log.RecordLog;

/**
 * Load registered init functions and execute in order.
 * <p>
 * 加载已注册的init函数并按顺序执行。
 * </p>
 *
 * @author Eric Zhao
 */
public final class InitExecutor {

    private static volatile boolean initialized = false;

    /**
     * If one {@link InitFunc} throws an exception, the init process
     * will immediately be interrupted and the application will exit.
     * <p>
     * 如果一个InitFunc抛出异常，init进程将立即中断，应用程序将退出。
     * </p>
     */
    public static void doInit() {
        if (initialized) {
            return;
        }
        try {
            //加载InitFunc的实现
            ServiceLoader<InitFunc> loader = ServiceLoader.load(InitFunc.class);
            //构建OrderWrapper链表节点由InitFunc实现以及他的优先级排序属性InitOrder完成。
            //用户可以自定义加入
            List<OrderWrapper> initList = new ArrayList<OrderWrapper>();
            for (InitFunc initFunc : loader) {
                insertSorted(initList, initFunc);
            }
            //调用并初始化
            for (OrderWrapper w : initList) {
                w.func.init();
            }
            initialized = true;
        } catch (Exception ex) {
            RecordLog.info("[Sentinel InitExecutor] Init failed", ex);
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 排序
     *
     * @param list
     * @param func
     */
    private static void insertSorted(List<OrderWrapper> list, InitFunc func) {
        int order = resolveOrder(func);
        int idx = 0;
        for (; idx < list.size(); idx++) {
            if (list.get(idx).getOrder() > order) {
                break;
            }
        }
        list.add(idx, new OrderWrapper(order, func));
    }

    /**
     * 解析是否带有@InitOrder, 并读取其值
     * <p>
     * 有则是value，否则是LOWEST_PRECEDENCE
     * </p>
     *
     * @param func
     * @return
     */
    private static int resolveOrder(InitFunc func) {
        if (!func.getClass().isAnnotationPresent(InitOrder.class)) {
            return InitOrder.LOWEST_PRECEDENCE;
        } else {
            return func.getClass().getAnnotation(InitOrder.class).value();
        }
    }

    private InitExecutor() {
    }

    /**
     * 简单的进行包装
     */
    private static class OrderWrapper {
        private final int order;
        private final InitFunc func;

        OrderWrapper(int order, InitFunc func) {
            this.order = order;
            this.func = func;
        }

        int getOrder() {
            return order;
        }

        InitFunc getFunc() {
            return func;
        }
    }
}
