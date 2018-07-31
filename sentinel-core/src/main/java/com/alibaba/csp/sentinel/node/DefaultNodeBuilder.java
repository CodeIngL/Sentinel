package com.alibaba.csp.sentinel.node;

import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;

/**
 * 默认的DefaultNode构建器
 * 用于构建DefaultNode
 * @author qinan.qn
 */
public class DefaultNodeBuilder implements NodeBuilder {

    @Override
    public DefaultNode buildTreeNode(ResourceWrapper id, ClusterNode clusterNode) {
        return new DefaultNode(id, clusterNode);
    }

    @Override
    public ClusterNode buildClusterNode() {
        return new ClusterNode();
    }

}
