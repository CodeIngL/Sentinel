package com.alibaba.csp.sentinel.command.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.command.vo.NodeVo;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import com.alibaba.fastjson.JSONArray;

/**
 * @author jialiang.linjl
 */
@CommandMapping(name = "clusterNode")
public class FetchSimpleClusterNodeCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        /*
         * type==notZero means nodes whose totalRequest <= 0 will be ignored.
         */
        String type = request.getParam("type");
        List<NodeVo> list = new ArrayList<NodeVo>();
        Map<ResourceWrapper, ClusterNode> map = ClusterBuilderSlot.getClusterNodeMap();
        if (map == null) {
            return CommandResponse.ofSuccess(JSONArray.toJSONString(list));
        }
        for (Map.Entry<ResourceWrapper, ClusterNode> entry : map.entrySet()) {
            if ("notZero".equalsIgnoreCase(type)) {
                if (entry.getValue().totalRequest() > 0) {
                    list.add(NodeVo.fromClusterNode(entry.getKey(), entry.getValue()));
                }
            } else {
                list.add(NodeVo.fromClusterNode(entry.getKey(), entry.getValue()));
            }
        }
        return CommandResponse.ofSuccess(JSONArray.toJSONString(list));
    }

}
