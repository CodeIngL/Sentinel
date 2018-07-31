package com.alibaba.csp.sentinel.slots.statistic.metric;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.slots.statistic.base.Window;
import com.alibaba.csp.sentinel.slots.statistic.base.WindowWrap;

/**
 * The basic metric class in Sentinel using a {@link WindowLeapArray} internal.
 * <p>使用WindowLeapArray内部的Sentinel中的基本度量标准类。</p>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class ArrayMetric implements Metric {

    private final WindowLeapArray data;

    /**
     * 窗口长度，间隔数
     * @param windowLength
     * @param interval
     */
    public ArrayMetric(int windowLength, int interval) {
        this.data = new WindowLeapArray(windowLength, interval);
    }

    /**
     * For unit test.
     * 为了单元测试
     */
    public ArrayMetric(WindowLeapArray array) {
        this.data = array;
    }

    @Override
    public long success() {
        data.currentWindow();
        long success = 0;

        List<Window> list = data.values();
        for (Window window : list) {
            success += window.success();
        }
        return success;
    }

    @Override
    public long maxSuccess() {
        data.currentWindow();
        long success = 0;

        List<Window> list = data.values();
        for (Window window : list) {
            if (window.success() > success) {
                success = window.success();
            }
        }
        return Math.max(success, 1);
    }

    @Override
    public long exception() {
        data.currentWindow();
        long exception = 0;
        List<Window> list = data.values();
        for (Window window : list) {
            exception += window.exception();
        }
        return exception;
    }

    @Override
    public long block() {
        data.currentWindow();
        long block = 0;
        List<Window> list = data.values();
        for (Window window : list) {
            block += window.block();
        }
        return block;
    }

    @Override
    public long pass() {
        data.currentWindow();
        long pass = 0;
        List<Window> list = data.values();

        for (Window window : list) {
            pass += window.pass();
        }
        return pass;
    }

    @Override
    public long rt() {
        data.currentWindow();
        long rt = 0;
        List<Window> list = data.values();
        for (Window window : list) {
            rt += window.rt();
        }
        return rt;
    }

    @Override
    public long minRt() {
        data.currentWindow();
        long rt = 4900;
        List<Window> list = data.values();
        for (Window window : list) {
            if (window.minRt() < rt) {
                rt = window.minRt();
            }
        }

        return Math.max(1, rt);
    }

    @Override
    public List<MetricNode> details() {
        List<MetricNode> details = new ArrayList<MetricNode>();
        data.currentWindow();
        for (WindowWrap<Window> window : data.list()) {
            if (window == null) {
                continue;
            }
            MetricNode node = new MetricNode();
            node.setBlockedQps(window.value().block());
            node.setException(window.value().exception());
            node.setPassedQps(window.value().pass());
            long passQps = window.value().success();
            node.setSuccessQps(passQps);
            if (passQps != 0) {
                node.setRt(window.value().rt() / passQps);
            } else {
                node.setRt(window.value().rt());
            }
            node.setTimestamp(window.windowStart());
            details.add(node);
        }

        return details;
    }

    @Override
    public Window[] windows() {
        data.currentWindow();
        return data.values().toArray(new Window[data.values().size()]);
    }

    @Override
    public void addException() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap.value().addException();
    }

    @Override
    public void addBlock() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap.value().addBlock();
    }

    @Override
    public void addSuccess() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap.value().addSuccess();
    }

    @Override
    public void addPass() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap.value().addPass();
    }

    @Override
    public void addRT(long rt) {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap.value().addRT(rt);
    }

    @Override
    public void debugQps() {
        data.currentWindow();
        StringBuilder sb = new StringBuilder();
        sb.append(Thread.currentThread().getId()).append("_");
        for (WindowWrap<Window> windowWrap : data.list()) {

            sb.append(windowWrap.windowStart()).append(":").append(windowWrap.value().pass()).append(":")
                    .append(windowWrap.value().block());
            sb.append(",");

        }
        System.out.println(sb);
    }

    @Override
    public long previousWindowBlock() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap = data.getPreviousWindow();
        if (wrap == null) {
            return 0;
        }
        return wrap.value().block();
    }

    @Override
    public long previousWindowPass() {
        WindowWrap<Window> wrap = data.currentWindow();
        wrap = data.getPreviousWindow();
        if (wrap == null) {
            return 0;
        }
        return wrap.value().pass();
    }

}
