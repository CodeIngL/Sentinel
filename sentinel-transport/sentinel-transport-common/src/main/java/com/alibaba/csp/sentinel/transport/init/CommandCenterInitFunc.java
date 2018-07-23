package com.alibaba.csp.sentinel.transport.init;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.transport.CommandCenter;

/**
 * @author Eric Zhao
 */
public class CommandCenterInitFunc implements InitFunc {

    @Override
    public void init() throws Exception {
        ServiceLoader<CommandCenter> loader = ServiceLoader.load(CommandCenter.class);
        Iterator<CommandCenter> iterator = loader.iterator();
        if (iterator.hasNext()) {
            CommandCenter commandCenter = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalStateException("Only single command center can be started");
            } else {
                commandCenter.beforeStart();
                commandCenter.start();
                RecordLog.info("[CommandCenterInit] Starting command center: "
                    + commandCenter.getClass().getCanonicalName());
            }
        }
    }
}
