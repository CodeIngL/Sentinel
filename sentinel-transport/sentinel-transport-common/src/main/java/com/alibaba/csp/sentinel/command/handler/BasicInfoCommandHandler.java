package com.alibaba.csp.sentinel.command.handler;

import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.csp.sentinel.util.HostNameUtil;

/**
 * The basic info command returns the runtime properties.
 *
 * @author Eric Zhao
 */
@CommandMapping(name = "basicInfo")
public class BasicInfoCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        return CommandResponse.ofSuccess(HostNameUtil.getConfigString());
    }
}
