package com.laoxin.rpc.http;

import com.laoxin.rpc.http.handler.RequestMappingMethodHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


@Slf4j
public class HttpProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {
        Object result = RequestMappingMethodHandler.getInstance().handlerRequest(method, args);
        return result;
    }


}
