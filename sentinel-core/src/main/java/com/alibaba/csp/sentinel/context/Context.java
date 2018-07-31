package com.alibaba.csp.sentinel.context;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.EntranceNode;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.nodeselector.NodeSelectorSlot;

/**
 * This class holds metadata of current invocation:<br/>
 * <p>
 * <ul>
 * <li>the {@link EntranceNode}: the root of the current invocation
 * tree.</li>
 * <li>the current {@link Entry}: the current invocation point.</li>
 * <li>the current {@link Node}: the statistics related to the
 * {@link Entry}.</li>
 * <li>the origin:The origin is useful when we want to control different
 * invoker/consumer separately. Usually the origin could be the Service Consumer's app name. </li>
 * </ul>
 * <p>
 * Each {@link SphU}#entry() or {@link SphO}#entry() should be in a {@link Context},
 * if we don't invoke {@link ContextUtil}#enter() explicitly, DEFAULT context will be used.
 * </p>
 * <p>
 * A invocation tree will be created if we invoke {@link SphU}#entry() multi times in
 * the same context.
 * </p>
 * <p>
 * Same resource in different context will count separately, see {@link NodeSelectorSlot}.
 * </p>
 * <p>
 * <p>
 * 该类包含当前调用的元数据：
 * <ul>
 * <li>the {@link EntranceNode}: 当前调用树的root。</li>
 * <li>the 当前{@link Entry}: 当前的调用点.</li>
 * <li>the 当前{@link Node}: 与{@link Entry}相关的统计信息.</li>
 * <li>the origin:当我们想要分别控制不同的调用者/消费者时，原点是有用的。 通常，原点可以是服务消费者的应用名称。</li>
 * </ul>
 * <p>
 * <p>
 * 每个{@link SphU}#entry()或{@link SphO}#entry()应该在{@link Context}中，如果我们没有显式调用{@link ContextUtil}#enter() ，将使用DEFAULT context。
 * <p>
 * 如果我们在同一个上下文中多次调用{@link SphU}#entry()，将创建一个调用树。
 * </p>
 * <p>
 * <p>
 * 不同上下文中的相同资源将单独计数，请参阅{@link NodeSelectorSlot}.
 * </p>
 *
 * @author jialiang.linjl
 * @author leyou(lihao)
 * @author Eric Zhao
 * @see ContextUtil
 * @see NodeSelectorSlot
 */
public class Context {

    /**
     * Context name.
     * 上下文的名字
     */
    private String name;

    /**
     * The entrance node of current invocation tree.
     * 当前调用树的entrance node
     * @see EntranceNode
     */
    private DefaultNode entranceNode;

    /**
     * Current processing entry.
     * 当前处理的Entry
     * @see com.alibaba.csp.sentinel.CtSph.CtEntry
     */
    private Entry curEntry;

    /**
     * the origin of this context, usually the origin is the Service Consumer's app name.
     */
    private String origin = "";

    public Context(DefaultNode entranceNode, String name) {
        super();
        this.name = name;
        this.entranceNode = entranceNode;
    }

    public String getName() {
        return name;
    }

    public Node getCurNode() {
        return curEntry.getCurNode();
    }

    public void setCurNode(Node node) {
        this.curEntry.setCurNode(node);
    }

    public Entry getCurEntry() {
        return curEntry;
    }

    public void setCurEntry(Entry curEntry) {
        this.curEntry = curEntry;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public double getOriginTotalQps() {
        return getOriginNode() == null ? 0 : getOriginNode().totalQps();
    }

    public double getOriginBlockedQps() {
        return getOriginNode() == null ? 0 : getOriginNode().blockedQps();
    }

    public double getOriginPassedReqQps() {
        return getOriginNode() == null ? 0 : getOriginNode().successQps();
    }

    public double getOriginPassedQps() {
        return getOriginNode() == null ? 0 : getOriginNode().passQps();
    }

    public long getOriginTotalRequest() {
        return getOriginNode() == null ? 0 : getOriginNode().totalRequest();
    }

    public long getOriginBlockedRequest() {
        return getOriginNode() == null ? 0 : getOriginNode().blockedRequest();
    }

    public double getOriginAvgRt() {
        return getOriginNode() == null ? 0 : getOriginNode().avgRt();
    }

    public int getOriginCurThreadNum() {
        return getOriginNode() == null ? 0 : getOriginNode().curThreadNum();
    }

    public DefaultNode getEntranceNode() {
        return entranceNode;
    }

    /**
     * Get the parent {@link Node} of the current.
     *
     * @return the parent node of the current.
     */
    public Node getLastNode() {
        if (curEntry != null && curEntry.getLastNode() != null) { //存在当前的entry使用entry的node
            return curEntry.getLastNode();
        } else {
            return entranceNode;
        }
    }

    public Node getOriginNode() {
        return curEntry == null ? null : curEntry.getOriginNode();
    }
}
