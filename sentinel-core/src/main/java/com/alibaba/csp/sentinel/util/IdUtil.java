package com.alibaba.csp.sentinel.util;

/**
 * @author qinan.qn
 */
public final class IdUtil {

    public static String truncate(String id) {
        IdLexer lexer = new IdLexer(id);
        StringBuilder sb = new StringBuilder();
        String r;
        String temp = "";
        while ((r = lexer.nextToken()) != null) {
            if ("(".equals(r) || ")".equals(r) || ",".equals(r)) {
                sb.append(temp).append(r);
                temp = "";
            } else if (!".".equals(r)) {
                temp = r;
            }
        }

        return sb.toString();
    }

    private static class IdLexer {
        private String id;
        private int idx = 0;

        IdLexer(String id) {
            this.id = id;
        }

        String nextToken() {
            int oldIdx = idx;
            String result = null;
            while (idx != id.length()) {
                char curChar = id.charAt(idx);
                if (curChar == '.' || curChar == '(' || curChar == ')' || curChar == ',') {
                    if (idx == oldIdx) {
                        result = String.valueOf(curChar);
                        ++idx;
                        break;
                    } else {
                        result = id.substring(oldIdx, idx);
                        break;
                    }
                }
                ++idx;
            }
            return result;
        }
    }

    private IdUtil() {}
}
