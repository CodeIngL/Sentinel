package com.alibaba.csp.sentinel.command.handler;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.fastjson.JSONObject;

/**
 * @author jialiang.linjl
 */
@CommandMapping(name = "systemStatus")
public class FetchSystemStatusCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {

        Map<String, Object> systemStatus = new HashMap<String, Object>();

        systemStatus.put("rqps", Constants.ENTRY_NODE.successQps());
        systemStatus.put("qps", Constants.ENTRY_NODE.passQps());
        systemStatus.put("b", Constants.ENTRY_NODE.blockedQps());
        systemStatus.put("r", Constants.ENTRY_NODE.avgRt());
        systemStatus.put("t", Constants.ENTRY_NODE.curThreadNum());

        return CommandResponse.ofSuccess(JSONObject.toJSONString(systemStatus));
    }
}
