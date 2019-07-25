## Grpc 关于Java的使用记录

### Grpc的原理
> 一个RPC框架必须有两个基础的组成部分：数据的序列化和进程数据通信的交互方式。

对于序列化gRPC采用了自家公司开源的Protobuf。什么是Protobuf？    
**Google Protocol Buffer(简称 Protobuf) 是一种与语言无关，平台无关的可扩展机制，用于序列化结构化数据。
使用Protocol Buffers 可以一次定义结构化的数据，然后可以使用特殊生成的源代码轻松地在各种数据流中使用各种语言编写和读取结构化数据。**

而关于进程间的通讯，无疑是Socket。Java方面gRPC同样使用了成熟的开源框架Netty。使用Netty Channel作为数据通道。传输协议使用了HTTP2。

通过以上的分析，我们可以将一个完整的gRPC流程总结为以下几步：
```text
通过.proto文件定义传输的接口和消息体。
通过protocol编译器生成server端和client端的stub程序。
将请求封装成HTTP2的Stream。
通过Channel作为数据通信通道使用Socket进行数据传输。
```
### 使用Java 结合 Grpc 实现远程服务调用  

下面创建一个SpringBoot 多模块项目 与 Grpc 结合实现远程服务调用  
项目结构：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g5ayjebk9uj30hu0c0mxk.jpg)

### SpringBoot-Grpc-Lib
定义了两个.proto文件用于测试，并配置Gradle 插件，以便快速生成Java相关的实体类。  
user.proto
```proto
syntax = "proto3";

option java_package = "com.dashuai.learning.grpc.lib.proto";

service User {
    rpc SaveUser (UserData) returns (UserResponse) {
    }
    rpc GetUser (UserRequest) returns (UserData) {
    }

}
message UserData {
    int32 id = 1;
    string name = 2;
    string sex = 3;
    int32 age = 4;
    string remark = 5;
}
message UserRequest {
    int32 id = 1;
}
message UserResponse {
    bool isSuccess = 1;
}
```
上述声明了两个RPC服务，分别是SaveUser()和GetUser()，顾名思义，保存一个用户和获取一个用户信息。
该模块项目中，我配置了protobuf-gradle-plugin插件方便构建生成对应的Java类，build.gradle如下:  
```gradle
apply plugin: 'com.google.protobuf'
buildscript {
    repositories {
        mavenLocal()
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public' }
    }
    dependencies {
        classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.1'
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.5.1"
    }
    plugins {
        grpc {
            artifact = 'io.grpc:protoc-gen-grpc-java:1.16.1'
        }
    }
    //配置生成输出目录
    generatedFilesBaseDir = "$projectDir/src"
    generateProtoTasks {
        all()*.plugins {
            grpc { outputSubDir = "java" }
        }
    }
}
sourceSets {
    main {
        proto {
            // In addition to the default 'src/main/proto'
            srcDir 'src/main/protobuf'
            srcDir 'src/main/protocolbuffers'
            include '**/*.protodevel'
        }
        java {
        }
    }
    test {
        proto {
            // In addition to the default 'src/test/proto'
            srcDir 'src/test/protocolbuffers'
        }
    }
}
```
配置无误后，点击右侧Tasks的GenerateProto生成对应的Java类
![](https://ws1.sinaimg.cn/large/006mOQRagy1g5b0wr3icpj309d0f10t6.jpg)  
上述配置的.proto对应生成的Java文件如下图：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g5b0yva346j30cn0ast93.jpg)  
到此，服务的定义就完成了，下面分别对定义的服务进行实现。  
### SpringBoot-Grpc-Server
对应的服务端，实现服务所做业务,业务实现代码如下，使用一个全局Map存放用户信息，模拟数据库保存。  
```java
@GrpcService(UserOuterClass.class)
public class UserService extends UserGrpc.UserImplBase {

    private final static Logger log = LoggerFactory.getLogger(UserService.class);

    private Map<Integer, UserOuterClass.UserData> map = new HashMap<>();

    @Override
    public void saveUser(UserOuterClass.UserData request, StreamObserver<UserOuterClass.UserResponse> responseObserver) {
        try {
            map.put(request.getId(), request);
        } catch (Exception e) {
            log.error("保存失败了!msg:{}", e.getMessage());
        }
        UserOuterClass.UserResponse.Builder builder = UserOuterClass.UserResponse.newBuilder().setIsSuccess(true);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();

    }

    @Override
    public void getUser(UserOuterClass.UserRequest request, StreamObserver<UserOuterClass.UserData> responseObserver) {
        responseObserver.onNext(map.get(request.getId()));
        responseObserver.onCompleted();
    }
}
```
### SpringBoot-Grpc-Client
对应的客户端，调用对应的服务  
```java
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
```

使用两个API调用测试，将请求体转换为JSON格式，在使用JsonFormat转换成对应的对象进行服务的调用，
这里我只是为了测试而这样做，实际使用可能有所区别。  
```java
   @PostMapping(value = "/user", produces = "application/json;charset=UTF-8")
    public boolean saveUser(HttpServletRequest request) throws IOException {
        String json = new String(IoUtils.toByteArray(request.getInputStream()), request.getCharacterEncoding());
        return userClientService.saveUser(ProtobufUtils.jsonToPf(json, UserOuterClass.UserData.newBuilder()));
    }

    @GetMapping(value = "/user/{id}")
    @ApiOperation(value = "获取User实例", notes = "获取用户信息", response = UserOuterClass.UserData.class)
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer")
    public ApiResult getUser(@PathVariable Integer id) throws InvalidProtocolBufferException {
        UserOuterClass.UserData userData = userClientService.getUserData(id);
        return ApiResult.prepare().success(ProtobufUtils.pfToJson(userData));
    }
```
调用测试,录入一个用户信息：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g5b2m13dluj30sy0i6758.jpg)
查询用户信息：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g5b2nmprjij30vw0h6t9x.jpg)

到此，调用流程演示完毕!     
测试项目的源码已上传到Github:  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Grpc  

参考链接：  
https://www.jianshu.com/p/30ef9b3780d9   
[Protocol Buffers 3 简明教程](https://juejin.im/post/5b852d476fb9a019e4505873)  