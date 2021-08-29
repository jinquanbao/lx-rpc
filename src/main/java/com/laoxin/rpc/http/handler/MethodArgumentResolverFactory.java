package com.laoxin.rpc.http.handler;


import com.laoxin.rpc.http.handler.resolver.RequestBodyMethodArgumentResolver;
import com.laoxin.rpc.http.handler.resolver.RequestHeaderMethodArgumentResolver;
import com.laoxin.rpc.http.handler.resolver.RequestParamMethodArgumentResolver;
import com.laoxin.rpc.http.handler.resolver.RequestPartMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

/***
 * the Factory provide MethodArgumentResolver interfaces
 * @author jinquanbao
 */
public class MethodArgumentResolverFactory {

    private static List<MethodArgumentResolver> argumentResolvers=new ArrayList<>();

    static {
        argumentResolvers.add(new RequestParamMethodArgumentResolver());
        argumentResolvers.add(new RequestBodyMethodArgumentResolver());
        argumentResolvers.add(new RequestHeaderMethodArgumentResolver());
        argumentResolvers.add(new RequestPartMethodArgumentResolver());
    }

    public static List<MethodArgumentResolver> getMethodArgumentResolvers(){
        return argumentResolvers;
    }

    public static MethodArgumentResolver getMethodArgumentResolver(Class clazz){
        return argumentResolvers.stream().filter(x->x.support(clazz)).findAny().orElseThrow(()-> new RuntimeException("clazz not support"));
    }

}
