package com.alibaba.csp.sentinel.slotchain;

import com.alibaba.csp.sentinel.EntryType;

/**
 * Common resource wrapper.
 *
 * @author qinan.qn
 * @author jialiang.linjl
 */
public class StringResourceWrapper extends ResourceWrapper {

    public StringResourceWrapper(String name, EntryType type) {
        if (name == null) {
            throw new IllegalArgumentException("Resource name cannot be null");
        }
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShowName() {
        return name;
    }

    @Override
    public EntryType getType() {
        return type;
    }
}
