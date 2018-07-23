package com.taobao.csp.sentinel.dashboard.discovery;

import java.util.Set;
import java.util.TreeSet;

public class AppInfo {

    private String app = "";

    private Set<MachineInfo> machines = new TreeSet<MachineInfo>();

    public AppInfo() {
    }

    public AppInfo(String app) {
        this.app = app;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public synchronized Set<MachineInfo> getMachines() {
        return machines;
    }

    @Override
    public String toString() {
        return "AppInfo{" + "app='" + app + ", machines=" + machines + '}';
    }

    public synchronized boolean addMachine(MachineInfo machineInfo) {
        machines.remove(machineInfo);
        return machines.add(machineInfo);
    }

}
