package com.alibaba.csp.sentinel.adapter.servlet.callback;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.adapter.servlet.util.FilterUtil;

/***
 * The default {@link UrlBlockHandler}.
 *
 * @author youji.zj
 */
public class DefaultUrlBlockHandler implements UrlBlockHandler {

    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Directly redirect to the default flow control (blocked) page or customized block page.
        FilterUtil.blockRequest(request, response);
    }
}
