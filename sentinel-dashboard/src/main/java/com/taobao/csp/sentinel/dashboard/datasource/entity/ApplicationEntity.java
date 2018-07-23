package com.taobao.csp.sentinel.dashboard.datasource.entity;

import java.util.Date;

import com.taobao.csp.sentinel.dashboard.discovery.AppInfo;

/**
 * @author leyou
 */
public class ApplicationEntity {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String app;
    private String activeConsole;
    private Date lastFetch;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getActiveConsole() {
        return activeConsole;
    }

    public Date getLastFetch() {
        return lastFetch;
    }

    public void setLastFetch(Date lastFetch) {
        this.lastFetch = lastFetch;
    }

    public void setActiveConsole(String activeConsole) {
        this.activeConsole = activeConsole;
    }

    public AppInfo toAppInfo() {
        AppInfo appInfo = new AppInfo();
        appInfo.setApp(app);

        return appInfo;
    }

    @Override
    public String toString() {
        return "ApplicationEntity{" +
            "id=" + id +
            ", gmtCreate=" + gmtCreate +
            ", gmtModified=" + gmtModified +
            ", app='" + app + '\'' +
            ", activeConsole='" + activeConsole + '\'' +
            ", lastFetch=" + lastFetch +
            '}';
    }
}
