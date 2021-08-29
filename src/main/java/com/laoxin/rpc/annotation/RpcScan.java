package com.laoxin.rpc.annotation;

import com.laoxin.rpc.http.HttpFactoryBean;
import com.laoxin.rpc.scanner.RpcClientScannerRegistry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/***
 * use this annotation register rpc interfaces as HttpProxy bean
 * @see RpcClientScannerRegistry ,HttpFactoryBean
 * @author jinquanbao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RpcClientScannerRegistry.class)
public @interface RpcScan {

    String[] value() default {};

    String[] basePackages() default {};

    Class<? extends Annotation> annotationTypeFilterClass() default RpcClient.class;

    Class<? extends FactoryBean> factoryBeanClass() default HttpFactoryBean.class;

    String serverNamePrefix() default "rpc.clients.";
}
