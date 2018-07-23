package com.alibaba.csp.sentinel.adapter.grpc;

import java.io.IOException;
import java.util.Collections;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.adapter.grpc.gen.FooRequest;
import com.alibaba.csp.sentinel.adapter.grpc.gen.FooResponse;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for {@link SentinelGrpcClientInterceptor}.
 *
 * @author Eric Zhao
 */
public class SentinelGrpcClientInterceptorTest {

    private final String resourceName = "com.alibaba.sentinel.examples.FooService/sayHello";
    private final int threshold = 2;

    private Server server;

    private void configureFlowRule() {
        FlowRule rule = new FlowRule()
            .setCount(threshold)
            .setGrade(RuleConstant.FLOW_GRADE_QPS)
            .setResource(resourceName)
            .setLimitApp("default")
            .as(FlowRule.class);
        FlowRuleManager.loadRules(Collections.singletonList(rule));
    }

    @Test
    public void testGrpcClientInterceptor() throws Exception {
        final int port = 19328;

        configureFlowRule();
        prepareServer(port);

        FooServiceClient client = new FooServiceClient("localhost", port, new SentinelGrpcClientInterceptor());
        final int total = 8;
        for (int i = 0; i < total; i++) {
            sendRequest(client);
        }
        ClusterNode clusterNode = ClusterBuilderSlot.getClusterNode(resourceName, EntryType.OUT);
        assertNotNull(clusterNode);

        assertEquals(total - threshold, clusterNode.blockedRequest() * 2);
        assertEquals(total, clusterNode.totalRequest() * 2);

        long totalQps = clusterNode.totalQps();
        long passQps = clusterNode.passQps();
        long blockedQps = clusterNode.blockedQps();
        assertEquals(total, totalQps);
        assertEquals(total - threshold, blockedQps);
        assertEquals(threshold, passQps);

        stopServer();
    }

    private void sendRequest(FooServiceClient client) {
        try {
            FooResponse response = client.sayHello(FooRequest.newBuilder().setName("Sentinel").setId(666).build());
            System.out.println(ClusterBuilderSlot.getClusterNode(resourceName, EntryType.OUT).avgRt());
            System.out.println("Response: " + response);
        } catch (StatusRuntimeException ex) {
            System.out.println("Blocked, cause: " + ex.getMessage());
        }
    }

    private void prepareServer(int port) throws IOException {
        if (server != null) {
            throw new IllegalStateException("Server already running!");
        }
        server = ServerBuilder.forPort(port)
            .addService(new FooServiceImpl())
            .build();
        server.start();
    }

    private void stopServer() {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}