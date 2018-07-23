package com.alibaba.csp.sentinel.demo.rocketmq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

public class PullConsumerDemo {

    private static final String KEY = String.format("%s:%s", Constants.TEST_GROUP_NAME, Constants.TEST_TOPIC_NAME);

    private static final Map<MessageQueue, Long> OFFSET_TABLE = new HashMap<MessageQueue, Long>();

    public static void main(String[] args) throws MQClientException {
        // First we init the flow control rule for Sentinel.
        initFlowControlRule();

        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(Constants.TEST_GROUP_NAME);

        consumer.start();

        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(Constants.TEST_TOPIC_NAME);
        for (MessageQueue mq : mqs) {
            System.out.printf("Consuming messages from the queue: %s%n", mq);
            SINGLE_MQ:
            while (true) {
                try {
                    PullResult pullResult =
                        consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 100);
                    if (pullResult.getMsgFoundList() != null) {
                        for (MessageExt msg : pullResult.getMsgFoundList()) {
                            doSomething(msg);
                        }
                    }

                    long nextOffset = pullResult.getNextBeginOffset();
                    putMessageQueueOffset(mq, nextOffset);
                    consumer.updateConsumeOffset(mq, nextOffset);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        consumer.shutdown();
    }

    private static void doSomething(MessageExt message) {
        Entry entry = null;
        try {
            ContextUtil.enter(KEY);
            entry = SphU.entry(KEY, EntryType.OUT);

            // Your business logic here.
            System.out.printf("[%d][%s] Receive New Messages: %s %n", System.currentTimeMillis(),
                Thread.currentThread().getName(), message);
            System.out.println(new String(message.getBody()));
        } catch (BlockException ex) {
            ex.printStackTrace();
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    private static void initFlowControlRule() {
        FlowRule rule = new FlowRule();
        rule.setResource(KEY);
        // Max QPS is 2.
        rule.setCount(2);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");

        // Enable rate limiting (uniform). Intervals between two incoming calls will be constant (1000/QPS ms).
        // In this example, intervals between two incoming calls (message consumption) will be 500 ms constantly.
        rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        // If more requests are coming, they'll be put into the waiting queue.
        // The queue has a queueing timeout. Requests that may exceed the timeout will be immediately blocked.
        // In this example, the timeout is 10s.
        rule.setMaxQueueingTimeMs(10 * 1000);
        FlowRuleManager.loadRules(Collections.singletonList(rule));
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = OFFSET_TABLE.get(mq);
        if (offset != null) {
            return offset;
        }

        return 0;
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        OFFSET_TABLE.put(mq, offset);
    }

}