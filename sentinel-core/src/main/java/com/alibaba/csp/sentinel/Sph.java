package com.alibaba.csp.sentinel;

import java.lang.reflect.Method;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * Interface to get {@link Entry} for resource protection. If any block criteria is met,
 * a {@link BlockException} or its subclasses will be thrown. Successfully getting a entry
 * indicates permitting the invocation pass.
 * <p>
 * <p>
 * 获取resource保护{@link Entry}的接口。 如果满足任何阻塞criteria，则抛出{@link BlockException} 或其子类。 成功获取entry表示允许调用传递。
 * </p>
 *
 * @author qinan.qn
 * @author jialiang.linjl
 * @author leyou
 */
public interface Sph {

    /**
     * Create a protected resource.
     * <p>
     * <p>
     * 创建一个受保护的`Resource`
     * </p>
     *
     * @param name the unique name of the protected resource
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(String name) throws BlockException;

    /**
     * Create a protected method.
     * <p>
     * 创建一个受保护的`Method`
     * </p>
     *
     * @param method the protected method
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(Method method) throws BlockException;

    /**
     * Create a protected method.
     * <p>
     * 创建一个受保护的`Method`,其资源量为count
     * </p>
     *
     * @param method the protected method
     * @param count  the count that the resource requires
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(Method method, int count) throws BlockException;

    /**
     * Create a protected resource.
     * <p>
     * 创建一个受保护的`Resource`，其资源量为count
     * </p>
     *
     * @param name  the unique string for the resource
     * @param count the count that the resource requires
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(String name, int count) throws BlockException;

    /**
     * Create a protected method.
     * <p>
     * 创建一个受保护的`Method`,其流动的方向是{@link EntryType}
     * </p>
     *
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(Method method, EntryType type) throws BlockException;

    /**
     * Create a protected resource.
     * <p>
     * 创建一个受保护的`resource`,其流动的方向是{@link EntryType}
     * </p>
     *
     * @param name the unique name for the protected resource
     * @param type the resource is an inbound or an outbound method. This is used
     *             to mark whether it can be blocked when the system is unstable
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(String name, EntryType type) throws BlockException;

    /**
     * Create a protected method.
     * <p>
     * 创建一个受保护的`method`,其流动的方向是{@link EntryType}，其资源量为count
     * </p>
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable
     * @param count  the count that the resource requires
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(Method method, EntryType type, int count) throws BlockException;

    /**
     * Create a protected resource.
     * <p>
     * 创建一个受保护的`method`,其流动的方向是{@link EntryType}，其资源量为count
     * </p>
     * @param name  the unique name for the protected resource
     * @param type  the resource is an inbound or an outbound method. This is used
     *              to mark whether it can be blocked when the system is unstable
     * @param count the count that the resource requires
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(String name, EntryType type, int count) throws BlockException;

    /**
     * Create a protected method.
     * <p>
     * 创建一个受保护的`method`,其流动的方向是{@link EntryType}，其资源量为count，参数是args
     * </p>
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable
     * @param count  the count that the resource requires
     * @param args   the parameters of the method. It can also be counted by setting
     *               hot parameter rule
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(Method method, EntryType type, int count, Object... args) throws BlockException;

    /**
     * Create a protected resource.
     * <p>
     * 创建一个受保护的`resource`,其流动的方向是{@link EntryType}，其资源量为count，参数是args
     * </p>
     * @param name  the unique name for the protected resource
     * @param type  the resource is an inbound or an outbound method. This is used
     *              to mark whether it can be blocked when the system is unstable
     * @param count the count that the resource requires
     * @param args  the parameters of the method. It can also be counted by setting
     *              hot parameter rule
     * @return entry get.
     * @throws BlockException if the block criteria is met
     */
    Entry entry(String name, EntryType type, int count, Object... args) throws BlockException;

}
