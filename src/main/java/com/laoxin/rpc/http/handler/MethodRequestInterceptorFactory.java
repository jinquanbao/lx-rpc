package com.laoxin.rpc.http.handler;

import java.util.ArrayList;
import java.util.List;

/***
 * register and provide MethodRequestInterceptor
 * @author jinquanbao
 */
public class MethodRequestInterceptorFactory {

    private static List<MethodRequestInterceptor> interceptors = new ArrayList<>(0);

    public synchronized static void register(MethodRequestInterceptor interceptor){
        if(null != interceptor){
            interceptors.add(interceptor);
        }
    }

    public static List<MethodRequestInterceptor> getInterceptors(){
        return interceptors;
    }
}
