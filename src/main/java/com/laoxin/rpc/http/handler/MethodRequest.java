package com.laoxin.rpc.http.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.util.*;

/***
 * execute http request params
 * @author jinquanbao
 */
public class MethodRequest {

    private Object requestBody;

    private Map<String,Object> uriMap;

    private HttpHeaders headers;

    private HttpMethod httpMethod;

    public MethodRequest(Object requestBody, Map<String, Object> uriMap, HttpHeaders headers, HttpMethod httpMethod) {
        this.requestBody = requestBody;
        this.httpMethod = httpMethod;
        if(uriMap == null){
            uriMap = new HashMap<>();
        }
        this.uriMap = uriMap;
        if(headers == null){
            headers = new HttpHeaders();
        }
        this.headers = headers;
    }

    public MethodRequest header(String name, String ... values){
        if(StringUtils.isEmpty(name)){
            return this;
        }
        if (values == null || (values.length == 1 && values[0] == null)) {
            headers.remove(name);
        } else {
            List<String> headers = new ArrayList<>();
            headers.addAll(Arrays.asList(values));
            this.headers.put(name, headers);
        }
        return this;
    }

    public MethodRequest uri(String name,String  value){
        if(StringUtils.isEmpty(name)){
            return this;
        }
        this.uriMap.put(name,value);
        return this;
    }

    public MethodRequest uri(Map<String,Object> uriMap){
        if(null == uriMap || uriMap.isEmpty()){
            return this;
        }
        this.uriMap.putAll(uriMap);
        return this;
    }

    public MethodRequest body(Object body){
        this.requestBody = body;
        return this;
    }

    public MethodRequest httpMethod(HttpMethod httpMethod){
        this.httpMethod = httpMethod;
        return this;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public Map<String, Object> getUriMap() {
        return uriMap;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
