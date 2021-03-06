package com.alibaba.csp.sentinel.command.handler;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.EntranceNode;
import com.alibaba.csp.sentinel.node.Node;

/**
 * @author qinan.qn
 */
@CommandMapping(name = "tree")
public class FetchTreeCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String id = request.getParam("id");

        StringBuilder sb = new StringBuilder();

        DefaultNode start = Constants.ROOT;

        if (id == null) {
            visitTree(0, start, sb);
        } else {
            boolean exactly = false;
            for (Node n : start.getChildList()) {
                DefaultNode dn = (DefaultNode)n;
                if (dn.getId().getName().equals(id)) {
                    visitTree(0, dn, sb);
                    exactly = true;
                    break;
                }
            }

            if (!exactly) {
                for (Node n : start.getChildList()) {
                    DefaultNode dn = (DefaultNode)n;
                    if (dn.getId().getName().contains(id)) {
                        visitTree(0, dn, sb);
                    }
                }
            }
        }
        sb.append("\r\n\r\n");
        sb.append(
            "t:threadNum  pq:passQps  bq:blockedQps  tq:totalQps  rt:averageRt  prq: passRequestQps 1mp:1m-passed "
                + "1mb:1m-blocked 1mt:1m-total").append("\r\n");
        return CommandResponse.ofSuccess(sb.toString());
    }

    private void visitTree(int level, DefaultNode node, /*@NonNull*/ StringBuilder sb) {
        for (int i = 0; i < level; ++i) {
            sb.append("-");
        }
        if (!(node instanceof EntranceNode)) {
            sb.append(String.format("%s(t:%s pq:%s bq:%s tq:%s rt:%s prq:%s 1mp:%s 1mb:%s 1mt:%s)",
                node.getId().getShowName(), node.curThreadNum(), node.passQps(),
                node.blockedQps(), node.totalQps(), node.avgRt(), node.successQps(),
                node.totalRequest() - node.blockedRequest(), node.blockedRequest(),
                node.totalRequest())).append("\n");
        } else {
            sb.append(String.format("EntranceNode: %s(t:%s pq:%s bq:%s tq:%s rt:%s prq:%s 1mp:%s 1mb:%s 1mt:%s)",
                node.getId().getShowName(), node.curThreadNum(), node.passQps(),
                node.blockedQps(), node.totalQps(), node.avgRt(), node.successQps(),
                node.totalRequest() - node.blockedRequest(), node.blockedRequest(),
                node.totalRequest())).append("\n");
        }
        for (Node n : node.getChildList()) {
            DefaultNode dn = (DefaultNode)n;
            visitTree(level + 1, dn, sb);
        }
    }
}
