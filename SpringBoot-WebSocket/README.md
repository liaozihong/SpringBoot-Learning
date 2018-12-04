## SpringBoot 集成 WebSocket
### 什么是WebSocket?
WebSocket是HTML5开始提供的一种浏览器与服务器间进行全双工通讯的网络技术。依靠这种技术可以实现客户端和服务器端的长连接，双向实时通信。  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fxukroxy5nj30i80dd0wz.jpg)  

WebSocket协议被设计来取代用HTTP作为传输层的双向通讯技术，基于大部分Web服务都是HTTP协议，WebSocket仍使用HTTP来作为初始的握手（handshake），在握手中对HTTP协议进行升级，当服务端收到这个HTTP的协议升级请求后，如果支持WebSocket协议则返回HTTP状态码101，这样，WebSocket的握手便成功了。  
### 特点
异步、事件触发  
可以发送文本，图片等流文件  
数据格式比较轻量，性能开销小，通信高效  
使用ws或者wss协议的客户端socket，能够实现真正意义上的推送功能  
缺点：  
部分浏览器不支持，浏览器支持的程度与方式有区别，需要各种兼容写法。

### 长连接
与 AJAX 轮训的方式差不多，但长连接不像 AJAX 轮训一样，而是采用的阻塞模型（一直打电话，没收到就不挂电话）；客户端发起连接后，如果没消息，就一直不返回 Response 给客户端。直到有消息才返回，返回完之后，客户端再次建立连接，周而复始。  
在没有 WebSocket 之前，大家常用的手段应该就是轮训了，比如每隔几秒发起一次请求，但这样带来的就是高性能开销，都知道一次 HTTP 响应是需要经过三次握手和四次挥手，远不如 TCP 长连接来的划算。  
### WebSocket 事件  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fxun79sohlj30ke05bmxb.jpg)  

### 与SpringBoot集成Demo
导入jar包，
```gradle
	compile group: 'org.springframework.boot', name: 'spring-boot-starter-websocket', version: '2.0.4.RELEASE'
```
**声明服务端点**  
```java
@RestController
@ServerEndpoint("/chat-room/{username}")
public class ChatRoomServerEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomServerEndpoint.class);

    /**
     * Open session.
     * 连接服务端，打开session
     *
     * @param username the 用户名
     * @param session  the 会话
     */
    @OnOpen
    public void openSession(@PathParam("username") String username, Session session) {
        LIVING_SESSIONS_CACHE.put(username, session);
        String message = "欢迎用户[" + username + "] 来到聊天室！";
        log.info(message);
        sendMessageAll(message);
        sendAllUser();
    }

    /**
     * On message.
     * 向客户端推送消息
     *
     * @param username the 用户名
     * @param message  the 消息
     */
    @OnMessage
    public void onMessage(@PathParam("username") String username, String message) {
        log.info("{}发送消息：{}", username, message);
        sendMessageAll("用户[" + username + "] : " + message);
    }

    /**
     * On close.
     * 连接关闭
     *
     * @param username the 用户名
     * @param session  the 会话
     */
    @OnClose
    public void onClose(@PathParam("username") String username, Session session) {
        //当前的Session 移除
        LIVING_SESSIONS_CACHE.remove(username);
        //并且通知其他人当前用户已经离开聊天室了
        sendMessageAll("用户[" + username + "] 已经离开聊天室了！");
        sendAllUser();
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * On error.
     * 出现错误
     *
     * @param session   the session
     * @param throwable the throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }


    /**
     * On message.
     * 点到点推送消息
     *
     * @param sender  the 发送至
     * @param receive the 接受者
     * @param message the 消息
     */
    @GetMapping("/chat-room/{sender}/to/{receive}")
    public void onMessage(@PathVariable("sender") String sender, @PathVariable("receive") String receive, String message) {
        sendMessage(LIVING_SESSIONS_CACHE.get(receive), MessageType.MESSAGE,
                "[" + sender + "]" + "-> [" + receive + "] : " + message);
        log.info("[" + sender + "]" + "-> [" + receive + "] : " + message);
    }

}
```
**使用枚举类区分推送给前端类消息类型**
```java
public enum MessageType {

    /**
     * 用户名.
     */
    USERNAME("username"),

    /**
     * 普通消息.
     */
    MESSAGE("message");

    private String value;

    public String getValue() {
        return value;
    }

    MessageType(String value) {
        this.value = value;
    }
}
```
**推送消息工具类**
```java
public final class WebSocketUtils {

    /**
     * 模拟存储 websocket session 使用
     */
    public static final Map<String, Session> LIVING_SESSIONS_CACHE = new ConcurrentHashMap<>();

    /**
     * Send 消息至客户端
     *
     * @param message the message
     */
    public static void sendMessageAll(String message) {
        LIVING_SESSIONS_CACHE.forEach((sessionId, session) -> sendMessage(session, MessageType.MESSAGE, message));
    }

    /**
     * Send 所有用户名至客户端
     */
    public static void sendAllUser() {
        LIVING_SESSIONS_CACHE.forEach((sessionId, session) -> sendMessage(session,
                MessageType.USERNAME, LIVING_SESSIONS_CACHE.keySet()));
    }

    /**
     * 发送给指定用户消息
     *
     * @param session 用户 session
     * @param type    the type
     * @param message 发送内容
     */
    public static void sendMessage(Session session, MessageType type, Object message) {
        if (session == null) {
            return;
        }
        final RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            return;
        }
        try {
            String data = JSONParseUtils.object2JsonString(ApiResult.prepare().success(message, 200, type.getValue()));
            //向session推送数据
            basic.sendText(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
**配置WebSocket**
```java
@EnableWebSocket
@Configuration
public class WebSocketConfiguration {
    /**
     * Server endpoint exporter server endpoint exporter.
     *
     * @return the server endpoint exporter
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```
**聊天室页面**
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>基于WebSocket 简易聊天室</title>
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script type="text/javascript" src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
</head>
<body>
<form class="form-horizontal col-sm-8" role="form">
    <h2 class="center">聊&nbsp;&nbsp;天&nbsp;&nbsp;室&nbsp;</h2>
    <div class="form-group">
        <label for="message_content" class="col-sm-1 control-label">消息列表</label>
        <div class="col-sm-4">
    <textarea id="message_content" class="form-control" readonly="readonly"
              cols="57" rows="10">
</textarea>
        </div>
        <label for="user-list" class="col-sm-1 control-label">用户列表</label>
        <div class="col-sm-2">
    <textarea id="user-list" readonly="readonly" class="form-control"
              cols="8" rows="10"></textarea>
        </div>
    </div>

    <div class="form-group">
        <label for="in_user_name" class="col-sm-2 control-label">用户姓名 &nbsp;</label>
        <div class="col-sm-8">
            <input id="in_user_name" class="form-control" value=""/>
        </div>
        <button type="button" id="btn_join" class="btn btn-default">加入聊天室</button>
        <button type="button" id="btn_exit" class="btn btn-danger">离开聊天室</button>
    </div>

    <div class="form-group">
        <label for="in_room_msg" class="col-sm-2 control-label">群发消息 &nbsp;</label>
        <div class="col-sm-8">
            <input id="in_room_msg" class="form-control" value=""/>
        </div>
        <button type="button" id="btn_send_all" class="btn btn-default">发送消息</button>
    </div>

    <br/><br/><br/>

    <h3 class="center">好友聊天</h3>
    <br/>
    <div class="form-group">
        <label for="in_sender" class="col-sm-2 control-label">发送者 &nbsp;</label>
        <div class="col-sm-8">
            <input id="in_sender" class="form-control" value=""/>
        </div>
    </div>
    <div class="form-group">
        <label for="in_receive" class="col-sm-2 control-label">接受者 &nbsp;</label>
        <div class="col-sm-8">
            <input id="in_receive" class="form-control" value=""/>
        </div>
    </div>
    <div class="form-group">
        <label for="in_point_message" class="col-sm-2 control-label">发送消息 &nbsp;</label>
        <div class="col-sm-8">
            <input id="in_point_message" class="form-control" value=""/>
        </div>
        <button type="button" class="btn btn-default" id="btn_send_point">发送消息</button>
    </div>

</form>
</body>

<script type="text/javascript">
    $(document).ready(function () {
        var urlPrefix = 'ws://localhost:8080/chat-room/';
        var ws = null;
        $('#btn_join').click(function () {
            var username = $('#in_user_name').val();
            var url = urlPrefix + username;
            ws = new WebSocket(url);
            ws.onopen = function () {
                console.log("建立 websocket 连接...");
            };
            ws.onmessage = function (event) {
                var data = JSON.parse(event.data);
                console.info(data);
                if (data.msg === "message") {
                    //服务端发送的消息
                    $('#message_content').append(data.result + '\n');
                } else if (data.msg === "username") {
                    var result = '';
                    $.each(data.result, function (index, value) {
                        result += value + '\n';
                    })
                    $('#user-list').text(result);
                }
            };
            ws.onclose = function () {
                $('#message_content').append('用户[' + username + '] 已经离开聊天室!');
                console.log("关闭 websocket 连接...");
            }
        });
        //客户端发送消息到服务器
        $('#btn_send_all').click(function () {
            var msg = $('#in_room_msg').val();
            if (ws) {
                ws.send(msg);
            }
        });
        // 退出聊天室
        $('#btn_exit').click(function () {
            if (ws) {
                ws.close();
            }
        });

        $("#btn_send_point").click(function () {
            var sender = $("#in_sender").val();
            var receive = $("#in_receive").val();
            var message = $("#in_point_message").val();
            $.get("/chat-room/" + sender + "/to/" + receive + "?message=" + message, function () {
                console.info("发送成功!" + sender + "向" + receive + "发送了" + message);
            })
        })
    })
</script>
</html>
```
效果图：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fxuow9s1cfj30zt0mota4.jpg)
F12 查看WebSocket是否连接成功。  

源码已上传GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-WebSocket
参考链接：  
https://blog.battcn.com/2018/06/27/springboot/v2-other-websocket/  
https://www.jianshu.com/p/04b1f4b8419f  

