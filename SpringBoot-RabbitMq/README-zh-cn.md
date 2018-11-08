所谓延时消息就是指当消息被发送以后，并不想让消费者立即拿到消息，而是等待指定时间后，消费者才拿到这个消息进行消费。  
RabbitMQ队列本身是没有直接实现支持延迟队列的功能，但可以通过它的Time-To-Live Extensions 与 Dead Letter Exchange 的特性模拟出延迟队列的功能。
原理图如下：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fx0sqzx5zsj30m808qab6.jpg)

参考链接：  
[RabbitMQ延迟队列](https://segmentfault.com/a/1190000015369917#articleHeader0)  
