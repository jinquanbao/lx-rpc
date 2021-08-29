package com.laoxin.rpc.http.handler;

import java.lang.annotation.Annotation;

/***
 * Resolver Method Arguments interface
 * @author jinquanbao
 */
public interface MethodArgumentResolver {

    boolean support(Class clazz);

    Annotation support(Annotation... annotations);

    MethodParamter convert(int index, Annotation annotation);

    Object resolver(Object[] args,MethodParamter[] methodParamters);
}
