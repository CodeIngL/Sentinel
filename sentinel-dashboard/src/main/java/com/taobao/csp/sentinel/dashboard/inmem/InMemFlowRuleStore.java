package com.taobao.csp.sentinel.dashboard.inmem;

import java.util.concurrent.atomic.AtomicLong;

import com.taobao.csp.sentinel.dashboard.datasource.entity.FlowRuleEntity;
import org.springframework.stereotype.Component;

/**
 * Store {@link FlowRuleEntity} in memory.
 *
 * @author leyou
 */
@Component
public class InMemFlowRuleStore extends InMemRepositoryAdapter<FlowRuleEntity> {
    private static AtomicLong ids = new AtomicLong(0);

    @Override
    protected long nextId() {
        return ids.incrementAndGet();
    }
}
