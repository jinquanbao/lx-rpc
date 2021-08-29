package com.laoxin.rpc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {


    @org.junit.Test
    public void test(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(TestConfig.class);
        ac.refresh();
        TestService bean = ac.getBean(TestService.class);
        bean.test();

    }
}
