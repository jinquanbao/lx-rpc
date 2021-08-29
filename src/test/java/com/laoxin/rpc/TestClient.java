package com.laoxin.rpc;

import com.laoxin.rpc.annotation.RpcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RpcClient(value = "user",url = "http://localhost:8080/")
@RequestMapping("/v1/user")
public interface TestClient {

    @RequestMapping(value = "/info",method = RequestMethod.POST)
    String test(@RequestParam("id") String id);


}
