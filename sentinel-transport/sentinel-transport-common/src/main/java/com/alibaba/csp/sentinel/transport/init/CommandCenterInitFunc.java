package com.alibaba.csp.sentinel.transport.init;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.transport.CommandCenter;

/**
 * 自带的InitFunc实现
 * @author Eric Zhao
 */
public class CommandCenterInitFunc implements InitFunc {

    @Override
    public void init() throws Exception {
        //spi加载CommandCenter实现
        ServiceLoader<CommandCenter> loader = ServiceLoader.load(CommandCenter.class);
        Iterator<CommandCenter> iterator = loader.iterator();
        if (iterator.hasNext()) {
            CommandCenter commandCenter = iterator.next(); //仅仅只能有一个实现
            if (iterator.hasNext()) {
                throw new IllegalStateException("Only single command center can be started");
            } else {
                commandCenter.beforeStart(); //开始前
                commandCenter.start(); //开始
                RecordLog.info("[CommandCenterInit] Starting command center: "
                    + commandCenter.getClass().getCanonicalName());
            }
        }
    }
}
