package com.dashuai.learning.thrift;

import com.dashuai.learning.thrift.client.RPCThriftClient;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RPCThriftClientTest {
    @Autowired
    RPCThriftClient rpcThriftClient;

    @Test
    public void test() throws TException {
        long start = System.currentTimeMillis();
        System.out.println("开始时间：" + start);
        rpcThriftClient.open();
        try {
            for (int i = 0; i < 1000; i++) {
                String result = rpcThriftClient.getRPCThriftService().getDate("你妹夫的" + i);
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("结束时间：" + end + ",总耗时长:" + (end - start));
    }
}
