package com.alibaba.csp.sentinel.transport.command;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandHandlerProvider;
import com.alibaba.csp.sentinel.transport.command.netty.HttpServer;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.transport.CommandCenter;

/**
 * Implementation of {@link CommandCenter} based on Netty HTTP library.
 *
 * @author Eric Zhao
 */
public class NettyHttpCommandCenter implements CommandCenter {

    private final HttpServer server = new HttpServer();

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    @Override
    public void start() throws Exception {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    server.start();
                } catch (Exception ex) {
                    RecordLog.info("Start netty server error", ex);
                    ex.printStackTrace();
                    System.exit(-1);
                }
            }
        });
    }

    @Override
    public void stop() throws Exception {
        server.close();
        pool.shutdownNow();
    }

    @Override
    public void beforeStart() throws Exception {
        // Register handlers
        Map<String, CommandHandler> handlers = CommandHandlerProvider.getInstance().namedHandlers();
        server.registerCommands(handlers);
    }
}
