package com.alibaba.boot.sentinel.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.boot.sentinel.property.SentinelProperties;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.mvc.AbstractMvcEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author duanling
 * @author Eric Zhao
 */
@ConfigurationProperties("endpoints.sentinel")
public class SentinelActuatorEndpoint extends AbstractMvcEndpoint {

    @Autowired
    private SentinelProperties sentinelProperties;

    public SentinelActuatorEndpoint() {
        super("/sentinel", false);
    }

    @RequestMapping
    @ResponseBody
    public Map<String, Object> invoke() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("version", this.getClass().getPackage().getImplementationVersion());
        result.put("properties", sentinelProperties);

        List<FlowRule> flowRules = FlowRuleManager.getRules();
        List<DegradeRule> degradeRules = DegradeRuleManager.getRules();
        List<SystemRule> systemRules = SystemRuleManager.getRules();

        result.put("flowRules", flowRules);
        result.put("degradeRules", degradeRules);
        result.put("systemRules", systemRules);

        return result;
    }
}
