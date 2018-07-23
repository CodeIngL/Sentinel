package com.alibaba.csp.sentinel.slots.clusterbuilder;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.Node;

/**
 * @author jialiang.linjl
 */
public class ClusterNodeBuilder {

    @Test
    public void clusterNodeBuilder_normal() throws Exception {
        ContextUtil.enter("entry1", "caller1");

        Entry nodeA = SphU.entry("nodeA");

        Node curNode = nodeA.getCurNode();
        assertTrue(curNode.getClass() == DefaultNode.class);
        DefaultNode dN = (DefaultNode)curNode;
        assertTrue(dN.getClusterNode().getOriginCountMap().containsKey("caller1"));
        assertTrue(nodeA.getOriginNode() == dN.getClusterNode().getOriginNode("caller1"));

        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();

        ContextUtil.enter("entry4", "caller2");

        nodeA = SphU.entry("nodeA");

        curNode = nodeA.getCurNode();
        assertTrue(curNode.getClass() == DefaultNode.class);
        DefaultNode dN1 = (DefaultNode)curNode;
        assertTrue(dN1.getClusterNode().getOriginCountMap().containsKey("caller2"));
        assertTrue(dN1 != dN);

        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();
    }

}
