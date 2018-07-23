package com.taobao.csp.sentinel.dashboard.datasource.entity;

import java.util.Date;

/**
 * @author leyou
 */
public interface RuleEntity {

    Long getId();

    void setId(Long id);

    String getApp();

    String getIp();

    Integer getPort();

    Date getGmtCreate();
}
