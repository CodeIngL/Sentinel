package com.alibaba.csp.sentinel.slotchain;

import com.alibaba.csp.sentinel.EntryType;

/**
 * A wrapper of resource name and {@link EntryType}.
 *
 * <p>
 *     resource名称和EntryType的包装器。
 * </p>
 * <p>
 *     一个resource是否同一个resource取决于变量`name`的String不变属性
 * </p>
 * <p>
 *     默认的传输方向是{@link EntryType#OUT}
 * </p>
 *
 * @author qinan.qn
 * @author jialiang.linjl
 */
public abstract class ResourceWrapper {

    protected String name;
    protected EntryType type = EntryType.OUT;

    public abstract String getName();

    public abstract String getShowName();

    /**
     * Get {@link EntryType} of this wrapper.
     *
     * @return {@link EntryType} of this wrapper.
     */
    public abstract EntryType getType();

    /**
     * Only {@link #getName()} is considered.
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Only {@link #getName()} is considered.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourceWrapper) {
            ResourceWrapper rw = (ResourceWrapper)obj;
            return rw.getName().equals(getName());
        }
        return false;
    }
}
