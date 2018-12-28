## Gradle 构建 SpringBoot 并打包镜像Demo

使用 gradle-docker 插件 

构建子模块镜像，在子模块的build.gralde配置插件  

具体参照，我写的一篇文章https://blog.csdn.net/qq_39211866/article/details/81123694  

配置完了，在主目录下构建，命令如下
```text
# 构建项目，忽略测试用例
gradle clean build -x test 
# 构建镜像
gradle SpringBoot-Builder-Docker:docker

```

![](https://ws1.sinaimg.cn/large/006mOQRagy1fymsk4xqzxj31gm04m755.jpg)  

构建成功：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fymsp7smuaj317q03sgmk.jpg)  

启动镜像：  
```text
docker run -p 8080:8080 -d docker/springboot-docker:1.0.0
```
-p 挂载宿主机端口；  
-d 后台启动容器。  


访问https://localhost:8080/,可以看到成功调用接口。    
![](https://ws1.sinaimg.cn/large/006mOQRagy1fymsra1jb6j30nu0aqq3o.jpg)  