package com.alibaba.csp.sentinel.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.EntranceNode;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;
import com.alibaba.csp.sentinel.slots.nodeselector.NodeSelectorSlot;

/**
 * Utility class to get or create {@link Context} in current thread.
 * <p>
 * <p>
 * Each {@link SphU}#entry() or {@link SphO}#entry() should be in a {@link Context}.
 * If we don't invoke {@link ContextUtil}#enter() explicitly, DEFAULT context will be used.
 * </p>
 * <p>
 * <p>
 * 用于在当前线程中获取或创建Context的实用程序类。
 * </p>
 * <p>
 * 每个{@link SphU}#entry()或{@link SphO}#entry()都应该在{@link Context}.中。 如果我们不显式调用{@link ContextUtil}#enter()，将使用DEFAULT上下文。
 * </p>
 *
 * @author jialiang.linjl
 * @author leyou(lihao)
 * @author Eric Zhao
 */
public class ContextUtil {

    /**
     * Store the context in ThreadLocal for easy access.
     * <p>
     * 将上下文存储在ThreadLocal中以便于访问。
     * </p>
     */
    private static ThreadLocal<Context> contextHolder = new ThreadLocal<Context>();

    /**
     * Holds all {@link EntranceNode}
     * <p>持有所有的{@link EntranceNode}</p>
     */
    private static volatile Map<String, DefaultNode> contextNameNodeMap = new HashMap<String, DefaultNode>();

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Context NULL_CONTEXT = new NullContext();

    /**
     * <p>
     * Enter the invocation context. The context is ThreadLocal, meaning that
     * each thread has it's own {@link Context}. New context will be created if
     * current thread doesn't have one.
     * </p>
     * <p>
     * A context will be related to a {@link EntranceNode}, which represents the entrance
     * of the invocation tree. New {@link EntranceNode} will be created if
     * current context does't have one. Note that same context name will share
     * same {@link EntranceNode} globally.
     * </p>
     * <p>
     * Note that each distinct {@code origin} of {@code name} will lead to creating a new
     * {@link Node}, meaning that total {@link Node} created will be of:<br/>
     * {@code distinct context name count * distinct origin count} <br/>
     * So when origin is too many, memory efficiency should be carefully considered.
     * </p>
     * <p>
     * Same resource in different context will count separately, see {@link NodeSelectorSlot}.
     * </p>
     * <p>
     * 进入调用上下文。 上下文是ThreadLocal，意味着每个线程都有自己的Context。 如果当前线程没有自己的{@link Context}，则将创建新上下文。
     * <p>
     * 上下文将与{@link EntranceNode}相关，{@link EntranceNode}表示调用树的入口。 如果当前上下文没有，则将创建新的{@link EntranceNode}。
     * 请注意，相同的上下文名称将全局共享相同的{@link EntranceNode}。
     * </p>
     * <p>
     * 请注意，每个不同{@code name}的 {@code origin}都会导致创建一个新{@link Node}，这意味着创建的{@link Node}总数将是：不同的上下文名称计数*不同的原点数。
     * 因此当原点太多时，应该仔细考虑内存效率。
     * </p>
     * <p>
     * 不同上下文中的相同资源将单独计数，请参阅NodeSelectorSlot。
     * <p>
     * </p>
     *
     * @param name   the context name.
     * @param origin the origin of this invocation, usually the origin could be the Service
     *               Consumer's app name. The origin is useful when we want to control different
     *               invoker/consumer separately. 这个调用的来源，通常可能是服务消费者的应用程序名称。
     *               当我们想要分别控制不同的调用者/消费者时，原点很有用。
     * @return The invocation context of the current thread.
     */
    static public Context enter(String name, String origin) {
        if (Constants.CONTEXT_DEFAULT_NAME.equals(name)) { //name不允许为default_context_name
            throw new ContextNameDefineException(
                    "The " + Constants.CONTEXT_DEFAULT_NAME + " can't be permit to defined!");
        }
        return trueEnter(name, origin);
    }

    /**
     * @param name   上下文的名字
     * @param origin 更高层次的命名空间
     * @return
     */
    protected static Context trueEnter(String name, String origin) {
        Context context = contextHolder.get();
        if (context == null) {//不存在，构建上下文，先找到是否有共享的EntranceNode
            Map<String, DefaultNode> localCacheNameMap = contextNameNodeMap;
            DefaultNode node = localCacheNameMap.get(name);
            if (node == null) { //不存在node
                if (localCacheNameMap.size() > Constants.MAX_CONTEXT_NAME_SIZE) { //大于最大值，直接返回NULL_CONTEXT
                    return NULL_CONTEXT;
                } else {
                    try {
                        LOCK.lock();//锁定构建
                        node = contextNameNodeMap.get(name);
                        if (node == null) {
                            if (contextNameNodeMap.size() > Constants.MAX_CONTEXT_NAME_SIZE) { //大于最大值
                                return NULL_CONTEXT;
                            } else {
                                node = new EntranceNode(new StringResourceWrapper(name, EntryType.IN), null); //构建Node
                                // Add entrance node.
                                //添加孩子节点
                                Constants.ROOT.addChild(node);

                                //替换掉原来的map
                                Map<String, DefaultNode> newMap = new HashMap<String, DefaultNode>(
                                        contextNameNodeMap.size() + 1);
                                newMap.putAll(contextNameNodeMap);
                                newMap.put(name, node);
                                contextNameNodeMap = newMap;
                            }
                        }
                    } finally {
                        LOCK.unlock();
                    }
                }
            }
            //构建上下文并置入ThreadLocal中
            context = new Context(node, name);
            context.setOrigin(origin);
            contextHolder.set(context);
        }

        return context;
    }

    /**
     * <p>
     * Enter the invocation context. The context is ThreadLocal, meaning that
     * each thread has it's own {@link Context}. New context will be created if
     * current thread doesn't have one.
     * </p>
     * <p>
     * A context will related to A {@link EntranceNode}, which is the entrance
     * of the invocation tree. New {@link EntranceNode} will be created if
     * current context does't have one. Note that same resource name will share
     * same {@link EntranceNode} globally.
     * </p>
     * <p>
     * Same resource in different context will count separately, see {@link NodeSelectorSlot}.
     * </p>
     *
     * @param name the context name.
     * @return The invocation context of the current thread.
     */
    public static Context enter(String name) {
        return enter(name, "");
    }

    /**
     * Exit context of current thread, that is removing {@link Context} in the
     * ThreadLocal.
     */
    public static void exit() {
        Context context = contextHolder.get();
        if (context != null && context.getCurEntry() == null) {
            contextHolder.set(null);
        }
    }

    /**
     * Get {@link Context} of current thread.
     *
     * @return context of current thread. Null value will be return if current
     * thread does't have context.
     */
    public static Context getContext() {
        return contextHolder.get();
    }
}
