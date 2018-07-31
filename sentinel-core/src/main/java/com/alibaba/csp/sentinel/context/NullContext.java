package com.alibaba.csp.sentinel.context;

import com.alibaba.csp.sentinel.Constants;

/**
 * If total {@link Context} exceed {@link Constants#MAX_CONTEXT_NAME_SIZE}, a
 * {@link NullContext} will get when invoke {@link ContextUtil}.enter(), means
 * no rules checking will do.
 * <p>
 *     如果总{@link Context} 超过{@link Constants#MAX_CONTEXT_NAME_SIZE}，则在调用{@link ContextUtil}.enter()时将获得{@link NullContext}，
 *     这意味着不会执行任何规则检查。
 * </p>
 * <p>
 *     空实现
 * </p>
 *
 * @author qinan.qn
 */
public class NullContext extends Context {

    public NullContext() {
        super(null, null);
    }

}
