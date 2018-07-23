package com.alibaba.boot.sentinel;

import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Eric Zhao
 */
@SpringBootApplication
@RestController
@PropertySource("classpath:web-servlet.properties")
public class SimpleWebApplication {

    @RequestMapping("/foo")
    public String foo() {
        return "Hello!";
    }

    @RequestMapping("/baz")
    public String baz() {
        ClusterNode node = ClusterBuilderSlot.getClusterNode("/foo");
        if (node == null) {
            return "/foo has not been called!";
        } else {
            return "/foo total request in metrics: " + node.totalRequest();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleWebApplication.class, args);
    }
}
