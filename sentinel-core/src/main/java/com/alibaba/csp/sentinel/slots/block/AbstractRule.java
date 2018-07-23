package com.alibaba.csp.sentinel.slots.block;

/***
 * @author youji.zj
 */
public abstract class AbstractRule implements Rule {

    /*** 规则的资源描述 ***/
    private String resource;

    /*** 被限制的应用,授权时候为逗号分隔的应用集合，限流时为单个应用 ***/
    private String limitApp;

    public String getResource() {
        return resource;
    }

    public AbstractRule setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String getLimitApp() {
        return limitApp;
    }

    public AbstractRule setLimitApp(String limitApp) {
        this.limitApp = limitApp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractRule)) {
            return false;
        }

        AbstractRule that = (AbstractRule)o;

        if (resource != null ? !resource.equals(that.resource) : that.resource != null) {
            return false;
        }
        // if (limitApp != null ? !limitApp.equals(that.limitApp) :
        // that.limitApp != null) { return false; }
        if (!limitAppEquals(limitApp, that.limitApp)) {
            return false;
        }
        return true;
    }

    private boolean limitAppEquals(String str1, String str2) {
        if ("".equals(str1)) {
            return "default".equals(str2);
        } else if ("default".equals(str1)) {
            return "".equals(str2) || str2 == null || str1.equals(str2);
        }
        if (str1 == null) {
            return str2 == null || "default".equals(str2);
        }
        return str1.equals(str2);
    }

    public <T extends AbstractRule> T as(Class<T> clazz) {
        return (T)this;
    }

    @Override
    public int hashCode() {
        int result = resource != null ? resource.hashCode() : 0;
        // result = 31 * result + (limitApp != null ? limitApp.hashCode() : 0);
        if (!("".equals(limitApp) || "default".equals(limitApp) || limitApp == null)) {
            result = 31 * result + limitApp.hashCode();
        }
        return result;
    }
}
