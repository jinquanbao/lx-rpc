package com.laoxin.rpc.http.handler.resolver;

import com.laoxin.rpc.http.handler.MethodArgumentResolver;
import com.laoxin.rpc.http.handler.MethodParamter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;

/***
 * resovler method arguments mark by RequestPart annotation
 * @author jinquanbao
 */
public class RequestPartMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean support(Class clazz) {
        return RequestPart.class.isAssignableFrom(clazz);
    }

    @Override
    public Annotation support(Annotation... annotations) {
        if(null != annotations){
            for(Annotation annotation : annotations){
                if(annotation instanceof RequestPart){
                    return annotation;
                }
            }
        }
        return null;
    }

    @Override
    public MethodParamter convert(int paramterIndex, Annotation annotation) {
        MethodParamter paramter = new MethodParamter(paramterIndex);
        if(annotation instanceof RequestPart){
            RequestPart requestAnnotation = (RequestPart) annotation;
            paramter.setAnnotationType(RequestPart.class);
            paramter.setPatamterName(requestAnnotation.value());
        }
        return paramter;
    }

    @Override
    public Object resolver(Object[] args, MethodParamter[] methodParamters) {
        MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
        if(null == methodParamters){
            return result;
        }
        for(MethodParamter paramter : methodParamters){
            if(RequestPart.class.isAssignableFrom(paramter.getAnnotationType())
                    || RequestParam.class.isAssignableFrom(paramter.getAnnotationType())){

                Object arg = args[paramter.getParamterIndex()];
                if(arg instanceof MultipartFile){
                    result.add(paramter.getPatamterName(),
                            ((MultipartFile) arg).getResource());
                }else {
                    result.add(paramter.getPatamterName(),arg);
                }
            }
        }
        return result;
    }
}
