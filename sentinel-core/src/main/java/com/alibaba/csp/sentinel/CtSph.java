package com.alibaba.csp.sentinel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.context.NullContext;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slotchain.MethodResourceWrapper;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slotchain.StringResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.Rule;

/**
 * {@inheritDoc}
 * <p>
 * CtSph是Sph的唯一默认的接口
 * </p>
 *
 * @author jialiang.linjl
 * @author leyou(lihao)
 * @author Eric Zhao
 * @see Sph
 */
public class CtSph implements Sph {

    private static final Object[] OBJECTS0 = new Object[0];

    /**
     * Same resource({@link ResourceWrapper#equals(Object)}) will share the same
     * {@link ProcessorSlotChain}, no matter in which {@link Context}.
     * <p>
     * <p>
     * <p>
     * 相同的resource({@link ResourceWrapper#equals(Object)})将共享相同的{@link ProcessorSlotChain}，无论在哪个{@link Context}.中。
     * </p>
     */
    private static Map<ResourceWrapper, ProcessorSlotChain> chainMap
            = new HashMap<ResourceWrapper, ProcessorSlotChain>();

    private static final Object LOCK = new Object();

    /**
     * Do all {@link Rule}s checking about the resource.
     * <p>
     * <p>Each distinct resource will use a {@link ProcessorSlot} to do rules checking. Same resource will use
     * same {@link ProcessorSlot} globally. </p>
     * <p>
     * <p>Note that total {@link ProcessorSlot} count must not exceed {@link Constants#MAX_SLOT_CHAIN_SIZE},
     * otherwise no rules checking will do. In this condition, all requests will pass directly, with no checking
     * or exception.</p>
     * <p>
     * 执行所有{@link Rule}s检查resource。
     * <p>
     * 每个不同的资源将使用{@link ProcessorSlot}进行规则检查。 相同的资源将全局使用相同的{@link ProcessorSlot}。
     * <p>
     * 请注意，{@link ProcessorSlot}总计数不得超过{@link Constants#MAX_SLOT_CHAIN_SIZE}，
     * 否则无法进行规则检查。 在这种情况下，所有请求都将直接传递，没有检查或异常。
     *
     * @param resourceWrapper resource name
     * @param count           tokens needed
     * @param args            arguments of user method call
     * @return {@link Entry} represents this call
     * @throws BlockException if any rule's threshold is exceeded
     */
    public Entry entry(ResourceWrapper resourceWrapper, int count, Object... args) throws BlockException {
        //获得上下文
        Context context = ContextUtil.getContext();
        if (context instanceof NullContext) {
            // Init the entry only. No rule checking will occur.
            // 仅初始化entry。 不会进行rule检查。
            return new CtEntry(resourceWrapper, null, context);
        }

        if (context == null) { //上下文不存在，新建上下文{CONTEXT_DEFAULT_NAME}
            context = MyContextUtil.myEnter(Constants.CONTEXT_DEFAULT_NAME, "", resourceWrapper.getType());
        }

        // Global switch is close, no rule checking will do.
        // 全局开关是关闭的，不会进行rule检查。
        if (!Constants.ON) {
            return new CtEntry(resourceWrapper, null, context);
        }

        //寻找处理链
        ProcessorSlot<Object> chain = lookProcessChain(resourceWrapper);

        /*
         * Means processor size exceeds {@link Constants.MAX_ENTRY_SIZE}, no
         * rule checking will do.
         * <p>
         *     意味着处理器大小超过{@link Constants.MAX_ENTRY_SIZE}，没有规则检查。
         */
        if (chain == null) {
            return new CtEntry(resourceWrapper, null, context);
        }

        //有效的Entry
        Entry e = new CtEntry(resourceWrapper, chain, context);
        try {
            //链式处理
            chain.entry(context, resourceWrapper, null, count, args);
        } catch (BlockException e1) {
            //存在异常，进行结束
            e.exit(count, args);
            throw e1;
        } catch (Throwable e1) {
            RecordLog.info("sentinel unexpected exception", e1);
        }
        return e;
    }

    /**
     * Get {@link ProcessorSlotChain} of the resource. new {@link ProcessorSlotChain} will
     * be created if the resource doesn't relate one.
     * <p>
     * <p>Same resource({@link ResourceWrapper#equals(Object)}) will share the same
     * {@link ProcessorSlotChain} globally, no matter in witch {@link Context}.<p/>
     * <p>
     * <p>
     * Note that total {@link ProcessorSlot} count must not exceed {@link Constants#MAX_SLOT_CHAIN_SIZE},
     * otherwise null will return.
     * </p>
     *
     * @param resourceWrapper target resource
     * @return {@link ProcessorSlotChain} of the resource
     */
    private ProcessorSlot<Object> lookProcessChain(ResourceWrapper resourceWrapper) {
        ProcessorSlotChain chain = chainMap.get(resourceWrapper); //尝试从缓存中获得
        if (chain == null) { //不存在进行初始化，整个map替换掉
            synchronized (LOCK) {
                chain = chainMap.get(resourceWrapper);
                if (chain == null) {
                    // Entry size limit.
                    if (chainMap.size() >= Constants.MAX_SLOT_CHAIN_SIZE) { //大于最大值，我们直接忽略掉
                        return null;
                    }

                    chain = Env.slotsChainbuilder.build(); //构建处理链
                    HashMap<ResourceWrapper, ProcessorSlotChain> newMap
                            = new HashMap<ResourceWrapper, ProcessorSlotChain>(
                            chainMap.size() + 1);
                    newMap.putAll(chainMap);
                    newMap.put(resourceWrapper, chain);
                    chainMap = newMap;
                }
            }
        }
        return chain;
    }

    /**
     * Entry的默认实现
     * <p>
     * 对于任意一个Entry他都有所属的Context，这决定他从属于哪一个Context
     * </p>
     */
    private static class CtEntry extends Entry {

        /**
         * Context下上级节点。
         */
        protected Entry parent = null;
        /**
         * 对this来说，为本级节点
         */
        protected Entry child = null;
        /**
         * 处理器
         */
        private ProcessorSlot<Object> chain;
        /**
         * 所属的上下文
         */
        private Context context;

        /**
         * entry的默认实现，唯一的实现方式
         *
         * @param resourceWrapper 资源
         * @param chain           处理链
         * @param context         上下文
         */
        CtEntry(ResourceWrapper resourceWrapper, ProcessorSlot<Object> chain, Context context) {
            super(resourceWrapper);
            this.chain = chain;
            this.context = context;
            parent = context.getCurEntry(); //获得上下文当前的entry作为自己的parent，可能为空，可能存在
            if (parent != null) {
                ((CtEntry) parent).child = this;
            }
            context.setCurEntry(this);//设置当前的上文的entry为自己
        }

        @Override
        public void exit(int count, Object... args) throws ErrorEntryFreeException {
            trueExit(count, args);
        }

        @Override
        protected Entry trueExit(int count, Object... args) throws ErrorEntryFreeException {
            if (context != null) {
                if (context.getCurEntry() != this) { //如果当前的entry和this不相同
                    // Clean previous call stack.
                    // 清理前一个调用堆栈。
                    CtEntry e = (CtEntry) context.getCurEntry();
                    while (e != null) {
                        e.exit(count, args);
                        e = (CtEntry) e.parent;
                    }
                    throw new ErrorEntryFreeException(
                            "The order of entry free is can't be paired with the order of entry");
                } else {
                    if (chain != null) {
                        chain.exit(context, resourceWrapper, count, args);
                    }
                    // Modify the call stack.
                    // 修改调用堆栈。
                    context.setCurEntry(parent);
                    if (parent != null) {
                        ((CtEntry) parent).child = null;
                    }
                    if (parent == null) {
                        // Auto-created entry indicates immediate exit.
                        // 自动创建的entry表示立即退出。
                        ContextUtil.exit();
                    }
                    // Clean the reference of context in current entry to avoid duplicate exit.
                    // 清除当前entry中的上下文引用以避免重复退出。
                    context = null;
                }
            }
            return parent;

        }

        @Override
        public Node getLastNode() {
            return parent == null ? null : parent.getCurNode();
        }
    }

    /**
     * This class is used for skip context name checking.
     * <p>
     * 此类用于跳过上下文名称检查。跳过对{@code Constants.CONTEXT_DEFAULT_NAME}
     * </p>
     */
    private final static class MyContextUtil extends ContextUtil {
        static Context myEnter(String name, String origin, EntryType type) {
            return trueEnter(name, origin);
        }
    }


    //简单的方式去实现接口，全部由入口 {@link  entry(ResourceWrapper, int, Object... ) }实现

    @Override
    public Entry entry(String name) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, EntryType.OUT);
        return entry(resource, 1, OBJECTS0);
    }

    @Override
    public Entry entry(Method method) throws BlockException {
        MethodResourceWrapper resource = new MethodResourceWrapper(method, EntryType.OUT);
        return entry(resource, 1, OBJECTS0);
    }

    @Override
    public Entry entry(Method method, EntryType type) throws BlockException {
        MethodResourceWrapper resource = new MethodResourceWrapper(method, type);
        return entry(resource, 1, OBJECTS0);
    }

    @Override
    public Entry entry(String name, EntryType type) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);
        return entry(resource, 1, OBJECTS0);
    }

    @Override
    public Entry entry(Method method, EntryType type, int count) throws BlockException {
        MethodResourceWrapper resource = new MethodResourceWrapper(method, type);
        return entry(resource, count, OBJECTS0);
    }

    @Override
    public Entry entry(String name, EntryType type, int count) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);
        return entry(resource, count, OBJECTS0);
    }

    @Override
    public Entry entry(Method method, int count) throws BlockException {
        MethodResourceWrapper resource = new MethodResourceWrapper(method, EntryType.OUT);
        return entry(resource, count, OBJECTS0);
    }

    @Override
    public Entry entry(String name, int count) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, EntryType.OUT);
        return entry(resource, count, OBJECTS0);
    }

    @Override
    public Entry entry(Method method, EntryType type, int count, Object... args) throws BlockException {
        MethodResourceWrapper resource = new MethodResourceWrapper(method, type);
        return entry(resource, count, args);
    }

    @Override
    public Entry entry(String name, EntryType type, int count, Object... args) throws BlockException {
        StringResourceWrapper resource = new StringResourceWrapper(name, type);
        return entry(resource, count, args);
    }
}
