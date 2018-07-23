package com.alibaba.csp.sentinel.command.handler;

import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.Constants;

/**
 * @author youji.zj
 */
@CommandMapping(name = "setSwitch")
public class OnOffSetCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String value = request.getParam("value");

        try {
            Constants.ON = Boolean.valueOf(value);
        } catch (Exception e) {
            RecordLog.info("Bad value when setting global switch", e);
        }

        String info = "Sentinel set switch value: " + value;
        RecordLog.info(info);

        return CommandResponse.ofSuccess(info);
    }
}
