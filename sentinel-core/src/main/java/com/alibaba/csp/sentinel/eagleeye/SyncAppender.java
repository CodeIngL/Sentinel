package com.alibaba.csp.sentinel.eagleeye;

/**
 * @author jifeng
 */
final class SyncAppender extends EagleEyeAppender {

    private final EagleEyeAppender delegate;
    private final Object lock = new Object();

    public SyncAppender(EagleEyeAppender delegate) {
        this.delegate = delegate;
    }

    @Override
    public void append(String log) {
        synchronized (lock) {
            delegate.append(log);
        }
    }

    @Override
    public void flush() {
        synchronized (lock) {
            delegate.flush();
        }
    }

    @Override
    public void rollOver() {
        synchronized (lock) {
            delegate.rollOver();
        }
    }

    @Override
    public void reload() {
        synchronized (lock) {
            delegate.reload();
        }
    }

    @Override
    public void close() {
        synchronized (lock) {
            delegate.close();
        }
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    @Override
    public String getOutputLocation() {
        return delegate.getOutputLocation();
    }

    @Override
    public String toString() {
        return "SyncAppender [appender=" + delegate + "]";
    }
}
