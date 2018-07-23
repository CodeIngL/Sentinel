package com.taobao.csp.sentinel.dashboard.inmem;

import java.util.concurrent.atomic.AtomicLong;

import com.taobao.csp.sentinel.dashboard.datasource.entity.DegradeRuleEntity;
import org.springframework.stereotype.Component;

/**
 * @author leyou
 */
@Component
public class InMemDegradeRuleStore extends InMemRepositoryAdapter<DegradeRuleEntity> {

    private static AtomicLong ids = new AtomicLong(0);

    @Override
    protected long nextId() {
        return ids.incrementAndGet();
    }
}
