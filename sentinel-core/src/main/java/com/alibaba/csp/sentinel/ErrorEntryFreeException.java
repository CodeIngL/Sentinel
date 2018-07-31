package com.alibaba.csp.sentinel;

/**
 * Represents order mismatch of resource entry and resource exit (pair mismatch).
 * <p>
 *     表示资源entry和资源exit（对不匹配）的顺序不匹配。
 * </p>
 *
 * @author qinan.qn
 */
public class ErrorEntryFreeException extends RuntimeException {

    public ErrorEntryFreeException(String s) {
        super(s);
    }
}
