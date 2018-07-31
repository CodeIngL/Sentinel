package com.alibaba.csp.sentinel;

/**
 * An enum marks resource invocation direction.
 *
 * <p>
 *     enum标记资源调用方向。
 * </p>
 *
 * @author jialiang.linjl
 */
public enum EntryType {
    /**
     * Inbound traffic
     */
    IN("IN"),
    /**
     * Outbound traffic
     */
    OUT("OUT");

    private final String name;

    EntryType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return name;
    }
}
