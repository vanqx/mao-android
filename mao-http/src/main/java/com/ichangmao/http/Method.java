package com.ichangmao.http;

/**
 * Created by yangchangmao on 2016/9/9.
 */
public enum Method {
    GET,
    PUT,
    POST,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
    PATCH,
    PROPFIND,
    PROPPATCH,
    MKCOL,
    MOVE,
    COPY,
    LOCK,
    UNLOCK;

    static Method lookup(String method) {
        if (method == null)
            return null;

        try {
            return valueOf(method);
        } catch (IllegalArgumentException e) {
            // TODO: Log it?
            return null;
        }
    }
}
