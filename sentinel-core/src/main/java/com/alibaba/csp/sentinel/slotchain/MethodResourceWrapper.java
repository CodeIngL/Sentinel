package com.alibaba.csp.sentinel.slotchain;

import java.lang.reflect.Method;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.util.IdUtil;
import com.alibaba.csp.sentinel.util.MethodUtil;

/**
 * Resource wrapper for method invocation.
 *
 * <p>
 *     method调用的ResourceWrapper，另外一个是{@link com.alibaba.csp.sentinel.slotchain.MethodResourceWrapper}.仅仅只有这两种方式
 * </p>
 * @see com.alibaba.csp.sentinel.Sph#entry(Method)
 * @see com.alibaba.csp.sentinel.Sph#entry(Method, EntryType)
 * @see com.alibaba.csp.sentinel.Sph#entry(Method, EntryType, int)
 * @see com.alibaba.csp.sentinel.Sph#entry(Method, EntryType, int, Object...)
 * @author qinan.qn
 */
public class MethodResourceWrapper extends ResourceWrapper {

    private transient Method method;

    public MethodResourceWrapper(Method method, EntryType type) {
        this.method = method;
        this.name = MethodUtil.getMethodName(method);
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String getShowName() {
        return IdUtil.truncate(this.name);
    }

    @Override
    public EntryType getType() {
        return type;
    }

}
