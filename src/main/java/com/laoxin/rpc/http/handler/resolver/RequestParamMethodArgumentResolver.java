package com.laoxin.rpc.http.handler.resolver;

import com.laoxin.rpc.http.handler.MethodArgumentResolver;
import com.laoxin.rpc.http.handler.MethodParamter;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/***
 * resovler method arguments mark by RequestParam annotation
 * @author jinquanbao
 */
public class RequestParamMethodArgumentResolver implements MethodArgumentResolver {

    @Override
    public boolean support(Class clazz) {
        return RequestParam.class.isAssignableFrom(clazz);
    }

    @Override
    public Annotation support(Annotation... annotations) {
        if(null != annotations){
            for(Annotation annotation : annotations){
                if(annotation instanceof RequestParam){
                    return annotation;
                }
            }
        }
        return null;
    }

    @Override
    public MethodParamter convert(int paramterIndex, Annotation annotation) {
        MethodParamter paramter = new MethodParamter(paramterIndex);
        if(annotation instanceof RequestParam){
            RequestParam requestParam = (RequestParam) annotation;
            paramter.setPatamterName(requestParam.value());
            paramter.setAnnotationType(RequestParam.class);
        }
        return paramter;
    }

    @Override
    public Object resolver(Object[] args, MethodParamter[] methodParamters) {
        Map<String,Object> uriMap = new HashMap<>();
        if(null == methodParamters){
            return uriMap;
        }
        for(MethodParamter paramter : methodParamters){
            if(RequestParam.class.isAssignableFrom(paramter.getAnnotationType()) ){
                uriMap.put(paramter.getPatamterName(),
                        args[paramter.getParamterIndex()]);
            }
        }
        return uriMap;
    }
}
