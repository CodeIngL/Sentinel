package com.alibaba.csp.sentinel.context;

/**
 * 异常定义,非法的名字，仅仅出现在名字使用{@link com.alibaba.csp.sentinel.Constants#CONTEXT_DEFAULT_NAME}的场景下
 *
 * @author qinan.qn
 */
public class ContextNameDefineException extends RuntimeException {

    public ContextNameDefineException(String message) {
        super(message);
    }
}
