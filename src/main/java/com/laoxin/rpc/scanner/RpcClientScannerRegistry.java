package com.laoxin.rpc.scanner;

import com.laoxin.rpc.annotation.RpcScan;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/***
 * A ImportBeanDefinitionRegistrar to allow annotation configuration of rpc server scanning.
 * @see ClassPathRpcClientScanner , HttpFactoryBean
 * @author jinquanbao
 */
public class RpcClientScannerRegistry implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes annoAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));

        ClassPathRpcClientScanner scanner = new ClassPathRpcClientScanner(registry,environment,resourceLoader);

        Class<? extends Annotation> annotationTypeFilterClass = annoAttrs.getClass("annotationTypeFilterClass");
        scanner.setAnnotationTypeFilterClass(annotationTypeFilterClass);

        Class<? extends FactoryBean> factoryBeanClass = annoAttrs.getClass("factoryBeanClass");
        scanner.setFactoryBeanClass(factoryBeanClass);

        List<String> basePackages = new ArrayList<String>();
        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        String serverNamePrefix = annoAttrs.getString("serverNamePrefix");
        scanner.setServerNamePrefix(serverNamePrefix);
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
