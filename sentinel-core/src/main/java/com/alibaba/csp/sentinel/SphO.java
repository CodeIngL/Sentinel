package com.alibaba.csp.sentinel;

import java.lang.reflect.Method;
import java.util.List;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;

/**
 * Conceptually, physical or logical resource that need protection should be
 * surrounded by an entry. The requests to this resource will be blocked if any
 * criteria is met, eg. when any {@link Rule}'s threshold is exceeded. Once blocked,
 * {@link SphU}#enter() will return false.
 * <p>
 * <p>
 * To configure the criteria, we can use <code>XXXRuleManager.loadRules()</code> to add rules. eg.
 * {@link FlowRuleManager#loadRules(List)}, {@link DegradeRuleManager#loadRules(List)},
 * {@link SystemRuleManager#loadRules(List)}.
 * </p>
 * <p>
 * <p>
 * Following code is an example. {@code "abc"} represent a unique name for the
 * protected resource:
 * </p>
 * <p>
 * <pre>
 * public void foo() {
 *    if (SphO.entry("abc")) {
 *        try {
 *            // business logic
 *        } finally {
 *            SphO.exit(); // must exit()
 *        }
 *    } else {
 *        // failed to enter the protected resource.
 *    }
 * }
 * </pre>
 * <p>
 * Make sure {@code SphO.entry()} and {@link SphO#exit()} be paired in the same thread,
 * otherwise {@link ErrorEntryFreeException} will be thrown.
 * <p>
 * <p>
 * 从概念上讲，需要保护的物理或逻辑resource应该由entry包围。
 * 如果满足任何criteria，将阻止对此资源的请求，例如。 何时超过任何{@link Rule}'s的阈值。
 * 一旦被阻止，{@link SphU}#enter()将返回false。
 * </p>
 * <p>
 * 要配置criteria，我们可以使用<code>XXXRuleManager.loadRules()</code> 来添加规则，例如。
 * {@link FlowRuleManager#loadRules(List)}, {@link DegradeRuleManager#loadRules(List)},
 * {@link SystemRuleManager#loadRules(List)}.
 * </p>
 * <p>
 * 以下代码是一个示例，“abc”表示受保护resource的唯一名称
 * </p>
 * <pre>
 * public void foo() {
 *    if (SphO.entry("abc")) {
 *        try {
 *            // business logic
 *        } finally {
 *            SphO.exit(); // must exit()
 *        }
 *    } else {
 *        // failed to enter the protected resource.
 *    }
 * }
 * </pre>
 *
 * @author jialiang.linjl
 * @author leyou
 * @see SphU
 */
public class SphO {

    private static final Object[] OBJECTS0 = new Object[0];

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name the unique name of the protected resource
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(String name) {
        return entry(name, EntryType.OUT, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(Method method) {
        return entry(method, EntryType.OUT, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param count  tokens required
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(Method method, int count) {
        return entry(method, EntryType.OUT, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name  the unique string for the resource
     * @param count tokens required
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(String name, int count) {
        return entry(name, EntryType.OUT, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable,
     *               only inbound traffic could be blocked by {@link SystemRule}
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(Method method, EntryType type) {
        return entry(method, type, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name the unique name for the protected resource
     * @param type the resource is an inbound or an outbound method. This is used
     *             to mark whether it can be blocked when the system is unstable,
     *             only inbound traffic could be blocked by {@link SystemRule}
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(String name, EntryType type) {
        return entry(name, type, 1, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable,
     *               only inbound traffic could be blocked by {@link SystemRule}
     * @param count  tokens required
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(Method method, EntryType type, int count) {
        return entry(method, type, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name  the unique name for the protected resource
     * @param type  the resource is an inbound or an outbound method. This is used
     *              to mark whether it can be blocked when the system is unstable,
     *              only inbound traffic could be blocked by {@link SystemRule}
     * @param count tokens required
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(String name, EntryType type, int count) {
        return entry(name, type, count, OBJECTS0);
    }

    /**
     * Checking all {@link Rule}s about the resource.
     *
     * @param name  the unique name for the protected resource
     * @param type  the resource is an inbound or an outbound method. This is used
     *              to mark whether it can be blocked when the system is unstable,
     *              only inbound traffic could be blocked by {@link SystemRule}
     * @param count tokens required
     * @param args  extra parameters.
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(String name, EntryType type, int count, Object... args) {
        try {
            Env.sph.entry(name, type, count, args);
        } catch (BlockException e) {
            return false;
        } catch (Throwable e) {
            RecordLog.info("[Sentinel] Fatal error", e);
            return true;
        }
        return true;
    }

    /**
     * Checking all {@link Rule}s about the protected method.
     *
     * @param method the protected method
     * @param type   the resource is an inbound or an outbound method. This is used
     *               to mark whether it can be blocked when the system is unstable,
     *               only inbound traffic could be blocked by {@link SystemRule}
     * @param count  tokens required
     * @param args   the parameters of the method.
     * @return true if no rule's threshold is exceeded, otherwise return false.
     */
    public static boolean entry(Method method, EntryType type, int count, Object... args) {
        try {
            Env.sph.entry(method, type, count, args);
        } catch (BlockException e) {
            return false;
        } catch (Throwable e) {
            RecordLog.info("[Sentinel] Fatal error", e);
            return true;
        }
        return true;
    }

    public static void exit(int count, Object... args) {
        ContextUtil.getContext().getCurEntry().exit(count, args);
    }

    public static void exit(int count) {
        ContextUtil.getContext().getCurEntry().exit(count, OBJECTS0);
    }

    public static void exit() {
        exit(1, OBJECTS0);
    }
}
