package com.alibaba.csp.sentinel.eagleeye;

public abstract class EagleEyeAppender {

    public abstract void append(String log);

    public void flush() {
        // do nothing
    }

    public void rollOver() {
        // do nothing
    }

    public void reload() {
        // do nothing
    }

    public void close() {
        // do nothing
    }

    public void cleanup() {
        // do nothing
    }

    public String getOutputLocation() {
        return null;
    }
}
