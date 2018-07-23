package com.alibaba.csp.sentinel.adapter.servlet.callback;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * The URL block handler handles requests when blocked.
 *
 * @author youji.zj
 */
public interface UrlBlockHandler {

    /**
     * Handle the request when blocked.
     *
     * @param request  Servlet request
     * @param response Servlet response
     * @throws IOException some error occurs
     */
    void blocked(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
