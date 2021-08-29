package com.laoxin.rpc.http.handler;

import com.laoxin.rpc.annotation.IgnoreMethodRequestInterceptor;
import com.laoxin.rpc.http.HttpProxyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/***
 * resovler class method mark by annotation, after handler request
 * @author jinquanbao
 */
@Slf4j
public class RequestMappingMethodHandler {

    private static Map<Method, RequestMappingMethodRequest> methodRequestMap = null;

    private static RestTemplate restTemplate = null;

    private RequestMappingMethodHandler(){
        methodRequestMap = new ConcurrentHashMap<>();
        if(restTemplate == null){
            restTemplate = new RestTemplate();
        }
    }

    private static class Singleton{
        public static RequestMappingMethodHandler instance = new RequestMappingMethodHandler();
    }

    public static RequestMappingMethodHandler getInstance(){
        return Singleton.instance;
    }

    public static void setRestTemplate(RestTemplate restTemplate){
        if(null != restTemplate){
            RequestMappingMethodHandler.restTemplate = restTemplate;
        }
    }


    /***
     * cache Method request mark by RpcServer annotation class
     * @author jinquanbao
     */
    public void registerClassMethods(Class<?> clazz, HttpProxyContext context){

        Method[] methods = clazz.getMethods();
        RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);

        String uri = null;
        RequestMethod requestMethod = null;
        String[] consumes = null;
        String[] produces = null;
        if(annotation != null){
            if(annotation.value() != null && annotation.value().length > 0){
                uri = annotation.value()[0];
            }
            if(annotation.method() != null && annotation.method().length>0){
                requestMethod = annotation.method()[0];
            }
            consumes = annotation.consumes();
            produces = annotation.produces();
        }

        String url = plusUrl(context.getUrl(), uri);


        for(Method method : methods){

            if(method.isBridge()|| method.isDefault() || method.isSynthetic()){
                continue;
            }
            //init Method Request
            RequestMappingMethodRequest requestMappingMethodRequest = new RequestMappingMethodRequest();
            requestMappingMethodRequest.setUrl(url)
                    .setRequestMethod(requestMethod)
                    .setConsumes(consumes)
                    .setProduces(produces)
                    .setMethodParamters(resolverMethodParamters(method))
            ;

            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if(requestMapping != null){

                //method annotation value is priority
                if(requestMapping.value() != null && requestMapping.value().length > 0){
                    requestMappingMethodRequest.setUrl(plusUrl(url,requestMapping.value()[0]));
                }
                if(requestMapping.method() != null && requestMapping.method().length>0){
                    requestMappingMethodRequest.setRequestMethod(requestMapping.method()[0]);
                }
                if(requestMapping.consumes() != null && requestMapping.consumes().length > 0){
                    requestMappingMethodRequest.setConsumes(requestMapping.consumes());
                }
                if(requestMapping.produces() != null && requestMapping.produces().length > 0){
                    requestMappingMethodRequest.setProduces(requestMapping.produces());
                }
            }
            methodRequestMap.put(method, requestMappingMethodRequest);
        }
    }

    private String plusUrl(String endpoint ,String uri){
        if(StringUtils.isEmpty(endpoint)){
            return uri;
        }else if(StringUtils.isEmpty(uri)){
            return endpoint;
        }else if(endpoint.endsWith("/") && uri.startsWith("/")){
            return endpoint + uri.substring(1);
        }else {
            return endpoint + uri;
        }
    }

    private MethodParamter[] resolverMethodParamters(Method method){

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        MethodParamter[] methodParamters = new MethodParamter[parameterAnnotations.length];

        for(int i = 0; i<parameterAnnotations.length; i++ ){
            Annotation[] annotations = parameterAnnotations[i];
            MethodParamter methodParamter = new MethodParamter(i);

            for(MethodArgumentResolver resolver : MethodArgumentResolverFactory.getMethodArgumentResolvers()){

                Annotation annotation = resolver.support(annotations);

                if(annotation == null){
                    continue;
                }
                //convert MethodParamter from Annotation
                methodParamter = resolver.convert(i,annotation);
                break;
            }
            if(StringUtils.isEmpty(methodParamter.getPatamterName())){
                methodParamter.setPatamterName(method.getParameters()[i].getName());
            }

            //MethodParamter order by method args
            methodParamters[i] = methodParamter;
        }
        return methodParamters;
    }

    /***
     * execute http request
     * @author jinquanbao
     */
    public Object handlerRequest(Method method,Object[] args){

        RequestMappingMethodRequest requestMappingMethodRequest = methodRequestMap.get(method);
        if(null == requestMappingMethodRequest){
            log.error("method not register, method = {}",method);
            throw new RuntimeException("method not register");
        }
        //ReturnType
        Type returnType = method.getGenericReturnType();

        //convert request params
        MethodRequest methodRequest = convertMethodRequest(requestMappingMethodRequest, args);

        if(!method.isAnnotationPresent(IgnoreMethodRequestInterceptor.class)){
            //execute interceptors
            MethodRequestInterceptorFactory.getInterceptors().forEach(interceptor -> interceptor.apply(methodRequest));
        }

        HttpEntity httpEntity = new HttpEntity(methodRequest.getRequestBody(),methodRequest.getHeaders());


        //exchange request
        ResponseEntity responseEntity = restTemplate
                .exchange(requestMappingMethodRequest.getUrl(),
                        methodRequest.getHttpMethod(), httpEntity,
                        ParameterizedTypeReference.forType(returnType),
                        methodRequest.getUriMap());

        return responseEntity.getBody();
    }

    private MethodRequest convertMethodRequest(RequestMappingMethodRequest requestMappingMethodRequest,Object[] args){

        //resolver header
        HttpHeaders httpHeaders = (HttpHeaders)MethodArgumentResolverFactory
                .getMethodArgumentResolver(RequestHeader.class)
                .resolver(args, requestMappingMethodRequest.getMethodParamters());

        Object body = null;
        //resolver uri
        Map<String,Object> uriMap = null;
        //check MultipartFile
        if(checkMultipart(requestMappingMethodRequest.getConsumes())){

            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

            //resolver MultipartFile
            body = MethodArgumentResolverFactory
                    .getMethodArgumentResolver(RequestPart.class)
                    .resolver(args, requestMappingMethodRequest.getMethodParamters());

        }else {
            if(null != requestMappingMethodRequest.getConsumes() && requestMappingMethodRequest.getConsumes().length >0){
                httpHeaders.set("Content-Type",requestMappingMethodRequest.getConsumes()[0]);
            }
            //resolver body
            body = MethodArgumentResolverFactory
                    .getMethodArgumentResolver(RequestBody.class)
                    .resolver(args, requestMappingMethodRequest.getMethodParamters());

            //resolver uri
            uriMap = (Map<String,Object>)MethodArgumentResolverFactory
                    .getMethodArgumentResolver(RequestParam.class)
                    .resolver(args, requestMappingMethodRequest.getMethodParamters());
        }

        //resolver httpMethod
        HttpMethod httpMethod = convertHttpMethod(requestMappingMethodRequest.getRequestMethod());

        return new MethodRequest(body,uriMap,httpHeaders,httpMethod);
    }

    private boolean checkMultipart(String[] consumes){
        if(null == consumes){
            return false;
        }
        return Stream.of(consumes).anyMatch(x-> MediaType.MULTIPART_FORM_DATA_VALUE.equals(x));
    }

    private HttpMethod convertHttpMethod(RequestMethod requestMethod){

        switch (requestMethod){
            case POST:
                return HttpMethod.POST;
            case GET:
                return HttpMethod.GET;
            case PUT:
                return HttpMethod.PUT;
            case DELETE:
                return HttpMethod.DELETE;
            case PATCH:
                return HttpMethod.PATCH;
            case HEAD:
                return HttpMethod.HEAD;
            case TRACE:
                return HttpMethod.TRACE;
            case OPTIONS:
                return HttpMethod.OPTIONS;
            default:
                log.error("unknow http request method={}",requestMethod);
                throw new RuntimeException("unknow http request method");
        }
    }
}
