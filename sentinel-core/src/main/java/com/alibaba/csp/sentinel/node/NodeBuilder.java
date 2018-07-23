package com.alibaba.csp.sentinel.node;

import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;

/**
 * Builds new {@link DefaultNode} and {@link ClusterNode}.
 *
 * @author qinan.qn
 */
public interface NodeBuilder {

    DefaultNode buildTreeNode(ResourceWrapper id, ClusterNode clusterNode);

    ClusterNode buildClusterNode();
}
