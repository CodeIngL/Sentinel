package com.taobao.csp.sentinel.dashboard.view.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taobao.csp.sentinel.dashboard.discovery.MachineDiscovery;
import com.taobao.csp.sentinel.dashboard.discovery.MachineInfo;

/**
 * @author leyou
 */
public class MachineInfoVo {
    private String app;
    private String hostname;
    private String ip;
    private Integer port;
    private Date version;
    private boolean health;

    public static List<MachineInfoVo> fromMachineInfoList(List<MachineInfo> machines) {
        List<MachineInfoVo> list = new ArrayList<>();
        for (MachineInfo machine : machines) {
            list.add(fromMachineInfo(machine));
        }
        return list;
    }

    public static MachineInfoVo fromMachineInfo(MachineInfo machine) {
        MachineInfoVo vo = new MachineInfoVo();
        vo.setApp(machine.getApp());
        vo.setHostname(machine.getHostname());
        vo.setIp(machine.getIp());
        vo.setPort(machine.getPort());
        vo.setVersion(machine.getVersion());
        if (System.currentTimeMillis() - machine.getVersion().getTime() < MachineDiscovery.MAX_CLIENT_LIVE_TIME_MS) {
            vo.setHealth(true);
        }
        return vo;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public boolean isHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }
}
