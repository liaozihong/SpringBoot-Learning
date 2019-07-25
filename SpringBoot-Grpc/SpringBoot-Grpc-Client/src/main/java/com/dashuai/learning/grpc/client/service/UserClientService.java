package com.dashuai.learning.grpc.client.service;

import com.dashuai.learning.grpc.lib.proto.UserGrpc;
import com.dashuai.learning.grpc.lib.proto.UserOuterClass;
import com.dashuai.learning.utils.json.JSONParseUtils;
import io.grpc.Channel;
import lombok.extern.slf4j.Slf4j;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserClientService {
    @GrpcClient("local-grpc-server")
    private Channel serverChannel;

    public boolean saveUser(UserOuterClass.UserData userData) {
        UserGrpc.UserBlockingStub userBlockingStub = UserGrpc.newBlockingStub(serverChannel);
        System.out.println(JSONParseUtils.object2JsonString(userData));
        UserOuterClass.UserResponse response = userBlockingStub.saveUser(userData);
        return response.getIsSuccess();
    }

    public UserOuterClass.UserData getUserData(int id) {
        UserGrpc.UserBlockingStub userBlockingStub = UserGrpc.newBlockingStub(serverChannel);
        UserOuterClass.UserData userData = userBlockingStub.getUser(UserOuterClass.UserRequest.newBuilder().setId(id).build());
        System.out.println(JSONParseUtils.object2JsonString(userData));
        return userData;

    }
}
