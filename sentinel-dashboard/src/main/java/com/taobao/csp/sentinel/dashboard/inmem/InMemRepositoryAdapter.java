package com.taobao.csp.sentinel.dashboard.inmem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.taobao.csp.sentinel.dashboard.datasource.entity.RuleEntity;
import com.taobao.csp.sentinel.dashboard.discovery.MachineInfo;

/**
 * @author leyou
 */
public abstract class InMemRepositoryAdapter<T extends RuleEntity> implements RuleRepository<T, Long> {
    /**
     * {@code <machine, <id, rule>>}
     */
    private Map<MachineInfo, Map<Long, T>> machineRules = new ConcurrentHashMap<>(16);
    private Map<Long, T> allRules = new ConcurrentHashMap<>(16);

    private static final int MAX_RULES_SIZE = 10000;

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(nextId());
        }
        allRules.put(entity.getId(), entity);
        machineRules.computeIfAbsent(MachineInfo.of(entity.getApp(), entity.getIp(), entity.getPort()),
            e -> new ConcurrentHashMap<>(32))
            .put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<T> saveAll(List<T> rules) {
        allRules.clear();
        machineRules.clear();

        if (rules == null) {
            return null;
        }
        List<T> savedRules = new ArrayList<>(rules.size());
        for (T rule : rules) {
            savedRules.add(save(rule));
        }
        return savedRules;
    }

    @Override
    public T delete(Long id) {
        T entity = allRules.remove(id);
        if (entity != null) {
            machineRules.get(MachineInfo.of(entity.getApp(), entity.getIp(), entity.getPort())).remove(id);
        }
        return entity;
    }

    @Override
    public T findById(Long id) {
        return allRules.get(id);
    }

    @Override
    public List<T> findAllByMachine(MachineInfo machineInfo) {
        Map<Long, T> entities = machineRules.get(machineInfo);
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.values().stream()
            .collect(Collectors.toList());
    }

    /**
     * Get next unused id.
     *
     * @return
     */
    abstract protected long nextId();
}
