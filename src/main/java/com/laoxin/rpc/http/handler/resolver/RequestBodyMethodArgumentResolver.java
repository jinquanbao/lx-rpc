package com.laoxin.rpc.http.handler.resolver;

import com.laoxin.rpc.http.handler.MethodArgumentResolver;
import com.laoxin.rpc.http.handler.MethodParamter;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;

/***
 * resovler method arguments mark by RequestBody annotation
 * @author jinquanbao
 */
public class RequestBodyMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean support(Class clazz) {
        return RequestBody.class.isAssignableFrom(clazz);
    }

    @Override
    public Annotation support(Annotation... annotations) {
        if(null != annotations){
            for(Annotation annotation : annotations){
                if(annotation instanceof RequestBody){
                    return annotation;
                }
            }
        }
        return null;
    }

    @Override
    public MethodParamter convert(int paramterIndex, Annotation annotation) {
        MethodParamter paramter = new MethodParamter(paramterIndex);
        if(annotation instanceof RequestBody){
            RequestBody requestBody = (RequestBody) annotation;
            paramter.setAnnotationType(RequestBody.class);
        }
        return paramter;
    }

    @Override
    public Object resolver(Object[] args, MethodParamter[] methodParamters) {
        Object result = null;
        if(null == methodParamters){
            return result;
        }

        for(MethodParamter paramter : methodParamters){
            if(RequestBody.class.isAssignableFrom(paramter.getAnnotationType()) ){
                result = args[paramter.getParamterIndex()];
                break;
            }
        }
        return result;
    }
}
