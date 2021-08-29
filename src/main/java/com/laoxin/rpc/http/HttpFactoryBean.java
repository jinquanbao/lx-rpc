package com.laoxin.rpc.http;

import com.laoxin.rpc.http.handler.RequestMappingMethodHandler;
import org.springframework.beans.factory.FactoryBean;

public class HttpFactoryBean<T> implements FactoryBean<T> {

    private Class<T> rpcClientInterface;

    private HttpProxyContext context;

    public HttpFactoryBean(Class<T> rpcClientInterface, HttpProxyContext context) {
        this.rpcClientInterface = rpcClientInterface;
        this.context = context;
    }

    @Override
    public T getObject() throws Exception {
        RequestMappingMethodHandler.getInstance().registerClassMethods(rpcClientInterface,context);
        return HttpProxyFactory.newProxy(rpcClientInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.rpcClientInterface;
    }




}
