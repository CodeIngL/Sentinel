package com.alibaba.csp.sentinel.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * Util class for processing {@link Method}.
 *
 * @author youji.zj
 */
public final class MethodUtil {

    private static Map<Method, String> methodNameMap = new HashMap<Method, String>();

    private static final Object LOCK = new Object();

    /**
     * Parse and get the method name.
     */
    public static String getMethodName(Method method) {
        String methodName = methodNameMap.get(method);
        if (methodName == null) {
            synchronized (LOCK) {
                methodName = methodNameMap.get(method);
                if (methodName == null) {
                    StringBuilder sb = new StringBuilder();

                    String className = method.getDeclaringClass().getName();
                    String name = method.getName();
                    Class<?>[] params = method.getParameterTypes();
                    sb.append(className).append(":").append(name);
                    sb.append("(");

                    int paramPos = 0;
                    for (Class<?> clazz : params) {
                        sb.append(clazz.getName());
                        if (++paramPos < params.length) {
                            sb.append(",");
                        }
                    }
                    sb.append(")");
                    methodName = sb.toString();

                    HashMap<Method, String> newMap = new HashMap<Method, String>(methodNameMap);
                    newMap.put(method, methodName);
                    methodNameMap = newMap;
                }
            }
        }
        return methodName;
    }
}
