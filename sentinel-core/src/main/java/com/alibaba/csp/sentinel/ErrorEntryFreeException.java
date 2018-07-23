package com.alibaba.csp.sentinel;

/**
 * Represents order mismatch of resource entry and resource exit (pair mismatch).
 *
 * @author qinan.qn
 */
public class ErrorEntryFreeException extends RuntimeException {

    public ErrorEntryFreeException(String s) {
        super(s);
    }
}
