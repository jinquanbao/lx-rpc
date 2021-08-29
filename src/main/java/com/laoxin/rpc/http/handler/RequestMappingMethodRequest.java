package com.laoxin.rpc.http.handler;

import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;

@Getter
public class RequestMappingMethodRequest {

    //请求url
    private String url;

    //请求方法
    private RequestMethod requestMethod;

    //请求参数
    private MethodParamter[] methodParamters;

    //所处理的Content-Type
    private String[] consumes;

    //可接收的Accept
    private String[] produces;

    public RequestMappingMethodRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public RequestMappingMethodRequest setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public RequestMappingMethodRequest setMethodParamters(MethodParamter[] methodParamters) {
        this.methodParamters = methodParamters;
        return this;
    }

    public RequestMappingMethodRequest setConsumes(String[] consumes) {
        this.consumes = consumes;
        return this;
    }

    public RequestMappingMethodRequest setProduces(String[] produces) {
        this.produces = produces;
        return this;
    }
}
