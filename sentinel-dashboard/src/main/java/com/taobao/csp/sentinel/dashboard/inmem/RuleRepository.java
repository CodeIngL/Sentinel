package com.taobao.csp.sentinel.dashboard.inmem;

import java.util.List;

import com.taobao.csp.sentinel.dashboard.discovery.MachineInfo;

/**
 * Interface to store and find rules.
 *
 * @author leyou
 */
public interface RuleRepository<T, ID> {
    /**
     * Save one.
     *
     * @param entity
     * @return
     */
    T save(T entity);

    /**
     * Save all.
     *
     * @param rules
     * @return rules saved.
     */
    List<T> saveAll(List<T> rules);

    /**
     * Delete by id
     *
     * @param id
     * @return entity deleted
     */
    T delete(ID id);

    /**
     * Find by id.
     *
     * @param id
     * @return
     */
    T findById(ID id);

    /**
     * Find all by machine.
     *
     * @param machineInfo
     * @return
     */
    List<T> findAllByMachine(MachineInfo machineInfo);

    ///**
    // * Find all by app and enable switch.
    // * @param app
    // * @param enable
    // * @return
    // */
    //List<T> findAllByAppAndEnable(String app, boolean enable);
}
