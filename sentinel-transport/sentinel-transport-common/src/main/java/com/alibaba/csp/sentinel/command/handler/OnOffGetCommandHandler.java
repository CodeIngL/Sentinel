package com.alibaba.csp.sentinel.command.handler;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;

/**
 * @author youji.zj
 */
@CommandMapping(name = "getSwitch")
public class OnOffGetCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        return CommandResponse.ofSuccess("Sentinel switch value: " + Constants.ON);
    }
}
