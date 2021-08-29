package com.laoxin.rpc.http;

import java.lang.reflect.Proxy;

public class HttpProxyFactory {


    public static <T> T newProxy(Class<T> clazz){

        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new HttpProxy());
    }


}
