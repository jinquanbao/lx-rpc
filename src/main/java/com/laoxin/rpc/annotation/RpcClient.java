package com.laoxin.rpc.annotation;


import java.lang.annotation.*;

/***
 * use this annotation mark rpc interface
 * @see com.laoxin.rpc.scanner.ClassPathRpcClientScanner
 * @author jinquanbao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface RpcClient {

    String value() default "";

    String url() default "";

}
