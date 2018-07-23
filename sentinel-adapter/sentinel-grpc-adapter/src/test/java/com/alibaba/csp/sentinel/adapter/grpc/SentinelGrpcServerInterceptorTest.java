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
 * Test cases for {@link SentinelGrpcServerInterceptor}.
 *
 * @author Eric Zhao
 */
public class SentinelGrpcServerInterceptorTest {

    private final String resourceName = "com.alibaba.sentinel.examples.FooService/anotherHello";
    private final int threshold = 4;

    private Server server;
    private FooServiceClient client;

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
    public void testGrpcServerInterceptor() throws Exception {
        final int port = 19329;
        client = new FooServiceClient("localhost", port);

        configureFlowRule();
        prepareServer(port);

        final int total = 8;
        for (int i = 0; i < total; i++) {
            sendRequest();
        }
        ClusterNode clusterNode = ClusterBuilderSlot.getClusterNode(resourceName, EntryType.IN);
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

    private void sendRequest() {
        try {
            FooResponse response = client.anotherHello(FooRequest.newBuilder().setName("Sentinel").setId(666).build());
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
            .intercept(new SentinelGrpcServerInterceptor())
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