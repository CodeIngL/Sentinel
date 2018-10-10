package com.alibaba.csp.sentinel.slots.block.flow.controller;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.flow.Controller;

/**
 * The principle idea comes from guava. However, the calculation of guava is
 * rate-based, which means that we need to translate rate to qps.
 * <p>
 * https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/SmoothRateLimiter.java
 * <p>
 * Requests arriving at the pulse may drag down long idle systems even though it
 * has a much larger handling capability in stable period. It usually happens in
 * scenarios that require extra time for initialization, for example, db
 * establishes a connection; connects to a remote service, and so on.
 * <p>
 * That’s why we need “warm up”.
 * <p>
 * Sentinel’s “warm up” implementation is based on Guava's algorithm. However,
 * unlike Guava's scenario, which is a “leaky bucket”, and is mainly used to
 * adjust the request interval, Sentinel is more focus on controlling the count
 * of incoming requests per second without calculating its interval.
 * <p>
 * Sentinel's "warm-up" implementation is based on the guava-based algorithm.
 * However, Guava’s implementation focus on adjusting the request interval, in
 * other words, a Leaky bucket. Sentinel pays more attention to controlling the
 * count of incoming requests per second without calculating its interval, it is
 * more like a “Token bucket.”
 * <p>
 * <p>
 * The remaining tokens in the bucket is used to measure the system utility.
 * Suppose a system can handle b requests per second. Every second b tokens will
 * be added into the bucket until the bucket is full. And when system processes
 * a request, it takes a token from the bucket. The more tokens left in the
 * bucket, the lower the utilization of the system; when the token in the token
 * bucket is above a certain threshold, we call it in a "saturation" state.
 * <p>
 * Base on Guava’s theory, there is a linear equation we can write this in the
 * form y = m * x + b where y (a.k.a y(x)), or qps(q)), is our expected QPS
 * given a saturated period (e.g. 3 minutes in), m is the rate of change from
 * our cold (minimum) rate to our stable (maximum) rate, x (or q) is the
 * occupied token.
 * <p>
 * <p>
 * 原理想法来自guava。然而，guava的计算是基于速率的，这意味着我们需要将速率转换为qps。 https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/SmoothRateLimiter.java到达脉冲的请求可能会拖累长闲置系统，即使它有很多稳定期内处理能力更强。它通常发生在需要额外时间进行初始化的场景中，例如，db建立连接;连接到远程服务，依此类推。这就是我们需要“热身”的原因。 Sentinel的“热身”实现基于Guava的算法。然而，与Guava的情况不同，它是一个“漏洞”，主要用于调整请求间隔，Sentinel更专注于控制每秒传入请求的数量而不计算其间隔。 Sentinel的“热身”实施基于基于番石榴的算法。但是，Guava的实施重点是调整请求间隔，换句话说，就是Leaky桶。 Sentinel更注重控制每秒传入请求的数量而不计算其间隔，它更像是“令牌桶”。存储桶中的其余令牌用于测量系统效用。假设一个系统每秒可以处理b个请求。每秒钟的b令牌都会被添加到桶中，直到桶装满为止。当系统处理请求时，它会从存储桶中获取令牌。存储桶中剩余的令牌越多，系统的利用率越低;当令牌桶中的令牌高于某个阈值时，我们将其称为“饱和”状态。根据番石榴的理论，我们可以用y = m * x + b的形式写出这个线性方程，其中y（又名y（x））或qps（q））是给定饱和周期的预期QPS（例如3分钟），m是从我们的冷（最小）速率到我们的稳定（最大）速率的变化率，x（或q）是被占用的令牌。
 * </p>
 * <p>
 * <p>
 * https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/SmoothRateLimiter.java
 * <p>
 * 到达脉冲的请求可能会拖累长闲置系统，
 * </p>
 * <p>
 * 即使它有很多稳定期内处理能力更强。
 * 它通常发生在需要额外时间进行初始化的场景中，例如，db建立连接;连接到远程服务，依此类推。
 * 这就是我们需要“热身”的原因。 Sentinel的“热身”实现基于Guava的算法。
 * 然而，与Guava的情况不同，它是一个“漏洞”，主要用于调整请求间隔，Sentinel更专注于控制每秒传入请求的数量而不计算其间隔。
 * Sentinel的“热身”实施基于基于guava的算法。但是，Guava的实施重点是调整请求间隔，换句话说，就是Leaky桶。
 * Sentinel更注重控制每秒传入请求的数量而不计算其间隔，它更像是“Token bucket.”。
 * 存储桶中的其余令牌用于测量系统效用。假设一个系统每秒可以处理b个请求。每秒钟的b令牌都会被添加到桶中，直到桶装满为止。
 * 当系统处理请求时，它会从存储桶中获取令牌。存储桶中剩余的令牌越多，系统的利用率越低;当令牌桶中的令牌高于某个阈值时，我们将其称为“饱和”状态。
 * 根据guava的理论，我们可以用y = m * x + b的形式写出这个线性方程，其中y（又名y（x））或qps（q））是给定饱和周期的预期QPS（例如3分钟），m是从我们的冷（最小）速率到我们的稳定（最大）速率的变化率，x（或q）是被占用的令牌。
 * </p>
 *
 * @author jialiang.linjl
 */
public class WarmUpController implements Controller {

    private double count;
    private int coldFactor;
    private int warningToken = 0;
    private int maxToken;
    private double slope;

    AtomicLong storedTokens = new AtomicLong(0);
    AtomicLong lastFilledTime = new AtomicLong(0);

    public WarmUpController(double count, int warmupPeriodInSec, int coldFactor) {
        construct(count, warmupPeriodInSec, coldFactor);
    }

    public WarmUpController(double count, int warmUpPeriodInMic) {
        construct(count, warmUpPeriodInMic, 3);
    }

    private void construct(double count, int warmUpPeriodInSec, int coldFactor) {

        if (coldFactor <= 1) {
            throw new RuntimeException("cold factor should be larget than 1");
        }

        this.count = count;

        this.coldFactor = coldFactor;

        // thresholdPermits = 0.5 * warmupPeriod / stableInterval.
        // warningToken = 100;
        warningToken = (int) (warmUpPeriodInSec * count) / (coldFactor - 1);
        // / maxPermits = thresholdPermits + 2 * warmupPeriod /
        // (stableInterval + coldInterval)
        // maxToken = 200
        maxToken = warningToken + (int) (2 * warmUpPeriodInSec * count / (1.0 + coldFactor));

        // slope
        // slope = (coldIntervalMicros - stableIntervalMicros) / (maxPermits
        // - thresholdPermits);
        slope = (coldFactor - 1.0) / count / (maxToken - warningToken);

    }

    @Override
    public boolean canPass(Node node, int acquireCount) {
        long passQps = node.passQps();

        long previousQps = node.previousPassQps();
        syncToken(previousQps);

        // 开始计算它的斜率
        // 如果进入了警戒线，开始调整他的qps
        long restToken = storedTokens.get();
        if (restToken >= warningToken) {
            long aboveToken = restToken - warningToken;
            // 消耗的速度要比warning快，但是要比慢
            // current interval = restToken*slope+1/count
            double warningQps = Math.nextUp(1.0 / (aboveToken * slope + 1.0 / count));
            if (passQps + acquireCount <= warningQps) {
                return true;
            }
        } else {
            if (passQps + acquireCount <= count) {
                return true;
            }
        }

        return false;
    }

    private void syncToken(long passQps) {
        long currentTime = TimeUtil.currentTimeMillis();
        currentTime = currentTime - currentTime % 1000;
        long oldLastFillTime = lastFilledTime.get();
        if (currentTime <= oldLastFillTime) {
            return;
        }

        long oldValue = storedTokens.get();
        long newValue = coolDownTokens(currentTime, passQps);

        if (storedTokens.compareAndSet(oldValue, newValue)) {
            long currentValue = storedTokens.addAndGet(0 - passQps);
            if (currentValue < 0) {
                storedTokens.set(0l);
            }
            lastFilledTime.set(currentTime);
        }

    }

    private long coolDownTokens(long currentTime, long passQps) {
        long oldValue = storedTokens.get();
        long newValue = oldValue;

        // 添加令牌的判断前提条件:
        // 当令牌的消耗程度远远低于警戒线的时候
        if (oldValue < warningToken) {
            newValue = (long) (oldValue + (currentTime - lastFilledTime.get()) * count / 1000);
        } else if (oldValue > warningToken) {
            if (passQps < (int) count / coldFactor) {
                newValue = (long) (oldValue + (currentTime - lastFilledTime.get()) * count / 1000);
            }
        }
        return Math.min(newValue, maxToken);
    }

}
