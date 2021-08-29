package com.laoxin.rpc.http.handler.resolver;

import com.laoxin.rpc.http.handler.MethodArgumentResolver;
import com.laoxin.rpc.http.handler.MethodParamter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.annotation.Annotation;

/***
 * resovler method arguments mark by RequestHeader annotation
 * @author jinquanbao
 */
public class RequestHeaderMethodArgumentResolver implements MethodArgumentResolver {

    @Override
    public boolean support(Class clazz) {
        return RequestHeader.class.isAssignableFrom(clazz);
    }

    @Override
    public Annotation support(Annotation... annotations) {
        if(null != annotations){
            for(Annotation annotation : annotations){
                if(annotation instanceof RequestHeader){
                    return annotation;
                }
            }
        }
        return null;
    }

    @Override
    public MethodParamter convert(int paramterIndex, Annotation annotation) {
        MethodParamter paramter = new MethodParamter(paramterIndex);
        if(annotation instanceof RequestHeader){
            RequestHeader requestParam = (RequestHeader) annotation;
            paramter.setPatamterName(requestParam.value());
            paramter.setAnnotationType(RequestHeader.class);
        }
        return paramter;
    }

    @Override
    public Object resolver(Object[] args, MethodParamter[] methodParamters) {
        HttpHeaders headerMap = new HttpHeaders();
        if(null == methodParamters){
            return headerMap;
        }
        for(MethodParamter paramter : methodParamters){
            if(RequestHeader.class.isAssignableFrom(paramter.getAnnotationType()) ){
                headerMap.add(paramter.getPatamterName(),
                        (String) args[paramter.getParamterIndex()]);
            }
        }
        return headerMap;
    }
}
