### Thrift 远程调用RPC框架
#### 什么是Thrift?
#### Thrift 的跨语言特性
thrift通过一个中间语言IDL(接口定义语言)来定义RPC的数据类型和接口,这些内容写在以.thrift结尾的文件中,然后通过特殊的编译器来生成不同语言的代码,以满足不同需要的开发者,比如java开发者,就可以生成java代码,c++开发者可以生成c++代码,生成的代码中不但包含目标语言的接口定义,方法,数据类型,还包含有RPC协议层和传输层的实现代码.  
#### Thrift 的协议栈结构
![](https://ws1.sinaimg.cn/large/006mOQRagy1g4k6m71xcwj30mo0hr0ub.jpg)  
thrift是一种c/s的架构体系.在最上层是用户自行实现的业务逻辑代码.第二层是由thrift编译器自动生成的代码，主要用于结构化数据的解析，发送和接收。TServer主要任务是高效的接受客户端请求，并将请求转发给Processor处理。Processor负责对客户端的请求做出响应，包括RPC请求转发，调用参数解析和用户逻辑调用，返回值写回等处理。从TProtocol以下部分是thirft的传输协议和底层I/O通信。TProtocol是用于数据类型解析的，将结构化数据转化为字节流给TTransport进行传输。TTransport是与底层数据传输密切相关的传输层，负责以字节流方式接收和发送消息体，不关注是什么数据类型。底层IO负责实际的数据传输，包括socket、文件和压缩数据流等。  

#### 入门小实例
使用IDEA,安装Thrift插件：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g4k6qj9kkyj30re01o0sl.jpg)  
前往Thrift下载[Thrift](http://thrift.apache.org/download )包，并配置环境变量。  
idea中配置thrift的安装目录：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g4k6siqe50j30lm06pmxl.jpg)
完事后，编写一个thrift小例子：  
```thrift
namespace java com.dashuai.learning.thrift.service
service RPCDateService{
    string getDate(1:string userName)
}
```
接着右键编译：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g4k6u8uf9ij30gd0iijtb.jpg)  
就会生成对应的java服务类。  

导入对应的jar包，进行server端和client端的编码,我下载的是最新的0.12.0 版本的thrift，所以使用jar包0.12.0的。  
```txt
    compile 'org.apache.thrift:libthrift:0.12.0'
```
server端和client端源码请前往github上查看，链接如下：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Thrift-Server    
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Thrift-Client  

服务端首次处理服务调用如下错误，但不影响，具体未详，还望大佬指出。  
 ![](https://ws1.sinaimg.cn/large/006mOQRagy1g4k9tbqnnzj311c05mjse.jpg)

参考链接：  
https://www.cnblogs.com/fingerboy/p/6424248.html  
[Thrift语法参考](https://www.cnblogs.com/yuananyun/p/5186430.html)
