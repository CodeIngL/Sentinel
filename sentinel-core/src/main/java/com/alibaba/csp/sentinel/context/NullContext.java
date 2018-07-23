package com.alibaba.csp.sentinel.context;

import com.alibaba.csp.sentinel.Constants;

/**
 * If total {@link Context} exceed {@link Constants#MAX_CONTEXT_NAME_SIZE}, a
 * {@link NullContext} will get when invoke {@link ContextUtil}.enter(), means
 * no rules checking will do.
 *
 * @author qinan.qn
 */
public class NullContext extends Context {

    public NullContext() {
        super(null, null);
    }

}
