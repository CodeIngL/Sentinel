package com.alibaba.csp.sentinel.command.handler;

import java.util.Map.Entry;

import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

/**
 * @author qinan.qn
 */
@CommandMapping(name = "cnode")
public class FetchClusterNodeHumanCommandHandler implements CommandHandler<String> {

    private final static String FORMAT = "%-4s%-80s%-10s%-10s%-10s%-11s%-9s%-6s%-10s%-11s%-9s%-11s";
    private final static int MAX_LEN = 79;

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String name = request.getParam("id");

        if (StringUtil.isEmpty(name)) {
            return CommandResponse.ofFailure(new IllegalArgumentException("Invalid parameter: empty clusterNode name"));
        }

        StringBuilder sb = new StringBuilder();

        int i = 0;
        int nameLength = 0;
        for (Entry<ResourceWrapper, ClusterNode> e : ClusterBuilderSlot.getClusterNodeMap().entrySet()) {
            if (e.getKey().getName().contains(name)) {
                int l = e.getKey().getShowName().length();
                if (l > nameLength) {
                    nameLength = l;
                }
                if (++i == 30) {
                    break;
                }
            }

        }
        nameLength = nameLength > MAX_LEN ? MAX_LEN : nameLength;
        String format = FORMAT.replaceAll("80", String.valueOf(nameLength + 1));

        sb.append(String.format(format, "idx", "id", "thread", "pass", "blocked", "success", "total", "aRt",
            "1m-pass", "1m-block", "1m-all", "exception")).append("\n");
        for (Entry<ResourceWrapper, ClusterNode> e : ClusterBuilderSlot.getClusterNodeMap().entrySet()) {
            if (e.getKey().getName().contains(name)) {
                ClusterNode node = e.getValue();
                String id = e.getKey().getShowName();
                int lenNum = (int)Math.ceil((double)id.length() / nameLength) - 1;

                sb.append(String.format(format, i + 1, lenNum == 0 ? id : id.substring(0, nameLength),
                    node.curThreadNum(), node.passQps(), node.blockedQps(), node.successQps(), node.totalQps(),
                    node.avgRt(), node.totalRequest() - node.blockedRequest(), node.blockedRequest(),
                    node.totalRequest(), node.exceptionQps())).append("\n");
                for (int j = 1; j <= lenNum; ++j) {
                    int start = nameLength * j;
                    int end = j == lenNum ? id.length() : nameLength * (j + 1);
                    sb.append(String.format(format, "", id.substring(start, end), "", "", "", "", "", "", "", "", "",
                        "", "", "")).append("\n");
                }

                if (++i == 30) {
                    break;
                }
            }
        }

        return CommandResponse.ofSuccess(sb.toString());
    }
}
