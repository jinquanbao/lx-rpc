package com.laoxin.rpc.http.handler;

@FunctionalInterface
public interface MethodRequestInterceptor {

    void apply(MethodRequest methodRequest);
}
