package com.alibaba.csp.sentinel.slotchain;

import com.alibaba.csp.sentinel.EntryType;

/**
 * Common resource wrapper.
 *
 * <p>
 *     name的ResourceWrapper(普通的ResourceWrapper)，另外一个是{@link com.alibaba.csp.sentinel.EntryType.StringResourceWrapper}.仅仅只有这两种方式
 * </p>
 *
 * @see com.alibaba.csp.sentinel.Sph#entry(String)
 * @see com.alibaba.csp.sentinel.Sph#entry(String, EntryType)
 * @see com.alibaba.csp.sentinel.Sph#entry(String, EntryType, int)
 * @see com.alibaba.csp.sentinel.Sph#entry(String, EntryType, int, Object...)
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
