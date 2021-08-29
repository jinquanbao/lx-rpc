package com.laoxin.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private TestClient client;

    public void test(){
        Object o = client.test("1");
    }
}
