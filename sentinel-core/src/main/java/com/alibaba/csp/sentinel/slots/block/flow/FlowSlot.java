package com.alibaba.csp.sentinel.slots.block.flow;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;

/**
 * <p>
 * Combined the runtime statistics collected from the previous
 * slots(NodeSelectorSlot, ClusterNodeBuilderSlot, and StatistcSlot), FlowSlot
 * will use pre-set rules to decide whether the incoming requests should be
 * blocked.
 * <p>
 * {@code SphU.entry (resourceName) }will throw FlowException if any rule is
 * triggered. user can customize his own logic by catching FlowException.
 * <p>
 * One resource can have multiple flow rules. FlowSlot traverses these rules
 * until one of them is triggered or all rules have been traversed.
 * <p>
 * Each FlowRule is mainly composed of the 2 factors: grade, strategy, path; we
 * can combine these factors to achieve different effects.
 * <p>
 * The grade is defined by the grade field in FlowRule. Here, 0 for thread
 * isolation and 1 for request count shaping. Both thread count and request
 * count are collected in real runtime, and we can view these statistics by
 * following command: {@code
 * curl http：// localhost：8719 / tree？type = root`
 * idx id    thread pass  blocked   success total aRt   1m-pass   1m-block   1m-all   exeption
 * 2   abc647 0      460    46          46   1    27      630       276        897      0
 * }
 * <p>
 * Thread for the count of threads that is currently processing the resource;
 * pass for the count of incoming request within one second; blocked for the
 * count of requests blocked within one second; success for the count of the
 * requests successfully within one second; RT for the average response time of
 * the requests within a second; total for the sum of incoming requests and
 * blocked requests within one second; 1m-pass is for the count of incoming
 * requests within one minute; 1m-block is for the count of a request blocked
 * within one minute; 1m -all is the total of incoming and blocked requests
 * within 1 minute; exception is for the count of exceptions in one second.
 * <p>
 * This stage is usually used to protect resources from occupying. If a resource
 * takes long time to finish, threads will begin to occupy. The longer the
 * response takes, the more threads occupy.
 * <p>
 * Besides counter, thread pool or semaphore can also be used to achieve this.
 * <p>
 * - Thread pool: Allocate a thread pool to handle these resource. When there is
 * no more idle thread in the pool, the request is rejected without affecting
 * other resources.
 * <p>
 * - Semaphore: Use semaphore to control the concurrent count of the threads in
 * this resource.
 * <p>
 * The benefit of using thread pool is that, it can walk away gracefully when
 * time out. But it also bring us the cost of context switch and additional
 * threads. If the incoming requests is already served in a separated thread,
 * for instance, a servelet request, it will almost double the threads count if
 * using thread pool.
 * <p>
 * ### QPS Shaping ### When qps exceeds the threshold, we will take actions to
 * control the incoming request, and is configured by "controlBehavior" field in
 * flowrule
 * <p>
 * 1. immediately reject（RuleConstant.CONTROL_BEHAVIOR_DEFAULT）
 * <p>
 * This is the default behavior. The exceeded request is rejected immediately
 * and the FlowException is thrown
 * <p>
 * 2. Warmup（RuleConstant.CONTROL_BEHAVIOR_WARM_UP）
 * <p>
 * If the usage of system has been low for a while, and a large amount of
 * requests comes, the system might not be able to handle all these requests at
 * once. However if we steady increase the incoming request, the system can warm
 * up and finally be able to handle all the requests.If the usage of system has
 * been low for a while, and a large amount of requests comes, the system might
 * not be able to handle all these requests at once. However if we steady
 * increase the incoming request, the system can warm up and finally be able to
 * handle all the requests. This warmup period can be configured by setting the
 * field "warmUpPeriodSec" in flow rule.
 * <p>
 * 3.Rate limiter(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER) This strategy
 * strictly controls the interval between requests. In other words, it allows
 * requests to pass at a stable rate.
 * <img src="https://github.com/alibaba/Sentinel/wiki/image/queue.gif" width=
 * "300" height="200" /> This strategy is an implement of leaky bucket
 * (https://en.wikipedia.org/wiki/Leaky_bucket). It is used to handle the
 * request at a stable rate and is often used in burst traffic. For instance,
 * Message. When a large number of requests beyond the system’s capacity arrive
 * at the same time, the system using this strategy will handle requests and its
 * fixed rate until all the requests have been processed or time out.
 * <p>
 * <p>
 * 结合从先前插槽（NodeSelectorSlot，ClusterNodeBuilderSlot和StatistcSlot）收集的运行时统计信息，FlowSlot将使用预设规则来决定是否应阻止传入请求。
 * 如果触发任何规则，SphU.entry（resourceName）将抛出FlowException。
 * 用户可以通过捕获FlowException来自定义自己的逻辑。一个资源可以有多个流规则。
 * FlowSlot遍历这些规则，直到触发其中一个规则或遍历所有规则。
 * 每个FlowRule主要由2个因素组成：等级，策略，路径;我们可以结合这些因素来实现不同的效果。
 * 等级由FlowRule中的等级字段定义。
 * 这里，0表示线程隔离，1表示请求计数整形。
 * 线程计数和请求计数都是在实际运行时收集的，我们可以通过以下命令查看这些统计信息：
 * {@code
 * curl http：// localhost：8719 / tree？type = root`
 * idx id    thread pass  blocked   success total aRt   1m-pass   1m-block   1m-all   exeption
 * 2   abc647 0      460    46          46   1    27      630       276        897      0
 * }
 * 当前正在处理资源的线程计数的线程;在一秒钟内传递进入请求的计数;阻止在一秒钟内被阻止的请求数量;在一秒钟内成功完成请求计数; RT表示请求在一秒内的平均响应时间;一秒钟内传入请求和被阻止请求总和的总和; 1m-pass用于在一分钟内计入传入的请求; 1m-block用于在一分钟内阻止请求的计数; 1m -all是1分钟内传入和阻止请求的总数;异常是指一秒钟内的异常计数。此阶段通常用于保护资源免受占用。如果资源需要很长时间才能完成，则线程将开始占用。响应时间越长，线程占用的越多。除了计数器，线程池或信号量也可用于实现此目的。
 * - 线程池：分配线程池来处理这些资源。如果池中没有空闲线程，则拒绝该请求而不影响其他资源。
 * - 信号量：使用信号量控制此资源中线程的并发计数。使用线程池的好处是，它可以在超时时优雅地离开。但它也为我们带来了上下文切换和额外线程的成本。如果传入的请求已经在一个单独的线程中提供，例如，一个servlet请求，如果使用线程池，它几乎会使线程数增加一倍。 ### QPS Shaping ###当qps超过阈值时，我们将采取措施来控制传入请求，并由flowrule 1中的“controlBehavior”字段配置。立即拒绝（RuleConstant.CONTROL_BEHAVIOR_DEFAULT）这是默认行为。超出的请求立即被拒绝并抛出FlowException 2. Warmup（RuleConstant.CONTROL_BEHAVIOR_WARM_UP）如果系统的使用率已经很低一段时间，并且有大量请求到来，则系统可能无法处理所有这些请求立刻。
 * 但是，如果我们稳定地增加传入的请求，系统可以预热并最终能够处理所有请求。如果系统的使用已经低了一段时间，并且大量的请求到来，系统可能无法一次处理所有这些请求。但是，如果我们稳定地增加传入请求，系统可以预热并最终能够处理所有请求。可以通过在流规则中设置字段“warmUpPeriodSec”来配置此预热期。 3.Rate限制器（RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER）这个策略
 * </p>
 * <p>
 * <p>
 * 一个专用于{@link FlowRule}检查的ProcessorSlot。
 * </p>
 * <p>
 * <p>
 * 通过一个全局的权限管理器(FlowRuleManager来共享配置)
 * </p>
 *
 * @author jialiang.linjl
 */
public class FlowSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, Object... args)
            throws Throwable {

        FlowRuleManager.checkFlow(resourceWrapper, context, node, count);

        fireEntry(context, resourceWrapper, node, count, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }

}
