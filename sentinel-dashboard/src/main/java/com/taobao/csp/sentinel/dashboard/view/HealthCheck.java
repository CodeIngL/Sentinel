package com.taobao.csp.sentinel.dashboard.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthCheck {
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public
    @ResponseBody
    String checkPreload() {
        return "success";
    }
}
