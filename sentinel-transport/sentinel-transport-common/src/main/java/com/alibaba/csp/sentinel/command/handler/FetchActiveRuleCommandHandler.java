package com.alibaba.csp.sentinel.command.handler;

import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;

/**
 * @author jialiang.linjl
 */
@CommandMapping(name = "getRules")
public class FetchActiveRuleCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String type = request.getParam("type");
        if ("flow".equalsIgnoreCase(type)) {
            return CommandResponse.ofSuccess(JSON.toJSONString(FlowRuleManager.getRules()));
        } else if ("degrade".equalsIgnoreCase(type)) {
            return CommandResponse.ofSuccess(JSON.toJSONString(DegradeRuleManager.getRules()));
        } else if ("authority".equalsIgnoreCase(type)) {
            return CommandResponse.ofSuccess(JSON.toJSONString(AuthorityRuleManager.getRules()));
        } else if ("system".equalsIgnoreCase(type)) {
            return CommandResponse.ofSuccess(JSON.toJSONString(SystemRuleManager.getRules()));
        } else {
            return CommandResponse.ofFailure(new IllegalArgumentException("invalid type"));
        }
    }

}
