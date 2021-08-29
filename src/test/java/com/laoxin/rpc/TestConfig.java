package com.laoxin.rpc;

import com.laoxin.rpc.annotation.RpcScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@RpcScan("com.laoxin.rpc")
@ComponentScan("com.laoxin.rpc")
public class TestConfig {
}
