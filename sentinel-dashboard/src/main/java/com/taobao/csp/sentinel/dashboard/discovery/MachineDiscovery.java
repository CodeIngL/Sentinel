package com.taobao.csp.sentinel.dashboard.discovery;

import java.util.List;
import java.util.Set;

public interface MachineDiscovery {

    long MAX_CLIENT_LIVE_TIME_MS = 1000 * 60 * 5;
    String UNKNOWN_APP_NAME = "UNKNOWN";

    List<String> getAppNames();

    Set<AppInfo> getBriefApps();

    AppInfo getDetailApp(String app);

    long addMachine(MachineInfo machineInfo);
}