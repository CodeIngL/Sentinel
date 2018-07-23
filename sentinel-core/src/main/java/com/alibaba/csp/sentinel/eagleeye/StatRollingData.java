package com.alibaba.csp.sentinel.eagleeye;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jifeng
 */
final class StatRollingData {

    private final StatLogger statLogger;

    private final long timeSlot;

    private final long rollingTimeMillis;

    private final ReentrantLock writeLock;

    private final Map<StatEntry, StatEntryFunc> statMap;

    StatRollingData(StatLogger statLogger, int initialCapacity, long timeSlot, long rollingTimeMillis) {
        this(statLogger, timeSlot, rollingTimeMillis,
            new ConcurrentHashMap<StatEntry, StatEntryFunc>(
                Math.min(initialCapacity, statLogger.getMaxEntryCount())));
    }

    private StatRollingData(StatLogger statLogger, long timeSlot, long rollingTimeMillis,
                            Map<StatEntry, StatEntryFunc> statMap) {
        this.statLogger = statLogger;
        this.timeSlot = timeSlot;
        this.rollingTimeMillis = rollingTimeMillis;
        this.writeLock = new ReentrantLock();
        this.statMap = statMap;
    }

    StatEntryFunc getStatEntryFunc(
        final StatEntry statEntry, final StatEntryFuncFactory factory) {
        StatEntryFunc func = statMap.get(statEntry);
        if (func == null) {
            StatRollingData clone = null;
            writeLock.lock();
            try {
                int entryCount = statMap.size();
                if (entryCount < statLogger.getMaxEntryCount()) {
                    func = statMap.get(statEntry);
                    if (func == null) {
                        func = factory.create();
                        statMap.put(statEntry, func);
                    }
                } else {
                    Map<StatEntry, StatEntryFunc> cloneStatMap =
                        new HashMap<StatEntry, StatEntryFunc>(statMap);
                    statMap.clear();

                    func = factory.create();
                    statMap.put(statEntry, func);
                    clone = new StatRollingData(statLogger, timeSlot, rollingTimeMillis, cloneStatMap);
                }
            } finally {
                writeLock.unlock();
            }

            if (clone != null) {
                StatLogController.scheduleWriteTask(clone);
            }
        }
        return func;
    }

    StatLogger getStatLogger() {
        return statLogger;
    }

    long getRollingTimeMillis() {
        return rollingTimeMillis;
    }

    long getTimeSlot() {
        return timeSlot;
    }

    int getStatCount() {
        return statMap.size();
    }

    Set<Entry<StatEntry, StatEntryFunc>> getStatEntrySet() {
        return statMap.entrySet();
    }
}
