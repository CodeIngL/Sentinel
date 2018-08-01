package com.alibaba.csp.sentinel.node;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * <p>
 * This class stores summary runtime statistics of the resource, including rt, thread count, qps
 * and so on. Same resource shares the same {@link ClusterNode} globally, no matter in witch
 * {@link com.alibaba.csp.sentinel.context.Context}.
 * </p>
 * <p>
 * To distinguish invocation from different origin (declared in
 * {@link ContextUtil#enter(String name, String origin)}),
 * one {@link ClusterNode} holds an {@link #originCountMap}, this map holds {@link StatisticNode}
 * of different origin. Use {@link #getOriginNode(String)} to get {@link Node} of the specific
 * origin.<br/>
 * Note that 'origin' usually is Service Consumer's app name.
 * </p>
 * <p>
 * <p>
 * 此类存储资源的摘要运行时统计信息，包括rt，线程计数，qps等。
 * 无论是 {@link com.alibaba.csp.sentinel.context.Context}，同一资源都在全局共享同一个{@link ClusterNode}。
 * </p>
 * <p>
 * 为区分不同来源的调用（在{@link ContextUtil#enter(String name, String origin)}中声明），
 * 一个{@link ClusterNode}包含{@link #originCountMap}，此映射包含不同origin的StatisticNode。 使用{@link #getOriginNode(String)} 获取特定"origin"的{@link Node}。
 * 请注意，"origin"通常是Service Consumer的应用程序名称。
 * </p>
 *
 * @author qinan.qn
 * @author jialiang.linjl
 */
public class ClusterNode extends StatisticNode {

    /**
     * <p>
     * the longer the application runs, the more stable this mapping will
     * become. so we don't concurrent map but a lock. as this lock only happens
     * at the very beginning while concurrent map will hold the lock all the
     * time
     * </p>
     */
    private HashMap<String, StatisticNode> originCountMap = new HashMap<String, StatisticNode>();
    private ReentrantLock lock = new ReentrantLock();

    /**
     * Get {@link Node} of the specific origin. Usually the origin is the Service Consumer's app name.
     * <p>
     * <p>
     * 获取特定原点的{@link Node}。 通常，原点是服务消费者的应用程序名称。
     * </p>
     *
     * @param origin The caller's name. It is declared in the
     *               {@link ContextUtil#enter(String name, String origin)}.
     * @return the {@link Node} of the specific origin.
     */
    public Node getOriginNode(String origin) {
        //不存在则构建，存在则获得
        StatisticNode statisticNode = originCountMap.get(origin);
        if (statisticNode == null) {
            try {
                lock.lock();
                statisticNode = originCountMap.get(origin);
                if (statisticNode == null) {
                    statisticNode = new StatisticNode();
                    HashMap<String, StatisticNode> newMap = new HashMap<String, StatisticNode>(
                            originCountMap.size() + 1);
                    newMap.putAll(originCountMap);
                    newMap.put(origin, statisticNode);
                    originCountMap = newMap;
                }
            } finally {
                lock.unlock();
            }
        }
        return statisticNode;
    }

    public synchronized HashMap<String, StatisticNode> getOriginCountMap() {
        return originCountMap;
    }

    /**
     * Add exception count only when {@code throwable} is not {@link BlockException#isBlockException(Throwable)}
     *
     * @param throwable
     * @param count     count to add.
     */
    public void trace(Throwable throwable, int count) {
        if (!BlockException.isBlockException(throwable)) {
            for (int i = 0; i < count; i++) {
                this.increaseExceptionQps();
            }
        }
    }
}
