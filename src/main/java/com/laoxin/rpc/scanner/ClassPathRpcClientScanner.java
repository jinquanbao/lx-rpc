package com.laoxin.rpc.scanner;

import com.laoxin.rpc.http.HttpFactoryBean;
import com.laoxin.rpc.http.HttpProxyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;

/***
 * scanner interfaces mark by RpcServer register to spring Collection
 * @author jinquanbao
 */
@Slf4j
public class ClassPathRpcClientScanner extends ClassPathBeanDefinitionScanner {

    private Class<?> factoryBeanClass;

    private boolean lazy;

    private Class<? extends Annotation> annotationTypeFilterClass;

    private String serverNamePrefix = "";

    public ClassPathRpcClientScanner(BeanDefinitionRegistry registry) {
        super(registry,false);
    }

    public ClassPathRpcClientScanner(BeanDefinitionRegistry registry, Environment environment, ResourceLoader resourceLoader) {
        super(registry, false, environment, resourceLoader);
    }

    public void setFactoryBeanClass(Class<?> factoryBeanClass) {
        this.factoryBeanClass = factoryBeanClass == null ? HttpFactoryBean.class : factoryBeanClass;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public void setAnnotationTypeFilterClass(Class<? extends Annotation> annotationTypeFilterClass) {
        this.annotationTypeFilterClass = annotationTypeFilterClass;
    }

    public void setServerNamePrefix(String serverNamePrefix) {
        this.serverNamePrefix = serverNamePrefix;
    }

    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        // if specified, use the given annotation and / or marker interface
        if (this.annotationTypeFilterClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationTypeFilterClass));
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            log.warn("No Rpc server client was found in '" + Arrays.toString(basePackages)
                    + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        AbstractBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (AbstractBeanDefinition) holder.getBeanDefinition();
            boolean scopedProxy = false;
            if (ScopedProxyFactoryBean.class.getName().equals(definition.getBeanClassName())) {
                definition = (AbstractBeanDefinition) Optional
                        .ofNullable(((RootBeanDefinition) definition).getDecoratedDefinition())
                        .map(BeanDefinitionHolder::getBeanDefinition).orElseThrow(() -> new IllegalStateException(
                                "The target bean definition of scoped proxy bean not found. Root bean definition[" + holder + "]"));
                scopedProxy = true;
            }
            if (scopedProxy) {
                continue;
            }
            String beanClassName = definition.getBeanClassName();
            log.debug("Creating Rpc FactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName);
            BeanMetadataAttribute metadataAttribute = definition.getMetadataAttribute(annotationTypeFilterClass.getCanonicalName());

            HttpProxyContext context = new HttpProxyContext();
            if (definition instanceof AnnotatedBeanDefinition) {
                AnnotationMetadata metadata = ((AnnotatedBeanDefinition) definition).getMetadata();

                context =  convertContext(metadata);
            }
            // the actual class of the bean is FactoryBean
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.getConstructorArgumentValues().addGenericArgumentValue(context);
            definition.setBeanClass(this.factoryBeanClass);

            // Attribute for MockitoPostProcessor
            definition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, beanClassName);
            //Constructor by type
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

            definition.setLazyInit(lazy);

            //default SCOPE_SINGLETON
            definition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        }
    }

    private HttpProxyContext convertContext(AnnotationMetadata metadata){

        HttpProxyContext context = new HttpProxyContext();

        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotationTypeFilterClass.getCanonicalName());
        //RpcServer annotationAttributes
        if(!Objects.isNull(annotationAttributes)){
            String url = (String) annotationAttributes.get("url");
            if(StringUtils.isEmpty(url)){
                String value = (String) annotationAttributes.get("value");
                context.setValue(value);
                url = getEndpoint(value);
            }
            context.setUrl(url);
        }
        return context;
    }

    private String getEndpoint(String value){
        if(StringUtils.isEmpty(value)){
            return null;
        }
        String property = getEnvironment().getProperty(serverNamePrefix + value, "");
        if(StringUtils.hasText(property)){
            return property;
        }
        return getEnvironment().getProperty(value,value);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            log.warn("Skipping Rpc FactoryBean with name '" + beanName + "' and '"
                    + beanDefinition.getBeanClassName() + "' RpcInterface" + ". Bean already defined with the same name!");
            return false;
        }
    }
}
