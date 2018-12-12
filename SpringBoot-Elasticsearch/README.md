[TOC]
### Elasticsearch简介
Elasticsearch是一个基于Lucene的搜索服务器。
它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。     

ES安装x-pack后，默认的账户有三个，如下：

账户名 | 默认密码 |	权限
---|---|---
elastic	| changeme |	
kibana	| changeme	|
logstash_system	| changeme |

注意登录Kibana时，最好使用 Elastic 的账号。
您需要使用Elastic用户/密码登录才能创建新用户。Kibana用户仅用于连接和与ES通信。  

需要知道的：  
9300端口： ES节点之间通讯使用  
9200端口： ES节点 和 外部 通讯使用  

另外elasticsearch.yml的配置需要添加
```
network.host: 0.0.0.0
```
让其可以外网访问。   

另外，注意JAVA SpringBoot集成Elaskicsearch时，需要注意版本相对应，

spring data elasticsearch |	elasticsearch
---|---
3.1.x | 6.2.2
3.0.x | 5.5.0
2.1.x | 2.4.0
2.0.x | 2.2.0
1.3.x |	1.5.2

### 与SpringBoot集成  
添加JAR包依赖：  
```
compile group: 'org.springframework.boot', name: 'spring-boot-starter-data-elasticsearch', version: '2.0.6.RELEASE'
compile 'org.elasticsearch.client:x-pack-transport:5.5.0'
```
#### 配置连接elasticsearch
由于使用了X-PACK插件，需要做密码验证。  

所以需要引入x-pack-transport包。  

需要密码验证，所以yml配置不了，需要配置Bean注入，如下：  
```
 @Bean
    public TransportClient transportClient() throws UnknownHostException {
        TransportClient client = new PreBuiltXPackTransportClient(Settings.builder()
                .put("cluster.name", "docker-cluster")
                .put("xpack.security.user", "elastic:changeme")
                .build())
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }
```
使用上面的Bean注入，便可通过验证，连接到elasticsearch。  
若无配置X-PACK,可直接配置yml或properties：  
```
# ES
spring:
  data:
    elasticsearch:
      repositories:
        enabled: true
      cluster-name: elasticsearch
      cluster-nodes: 120.132.29.37:9300
```
默认 9300 是 Java 客户端的端口。9200 是支持 Restful HTTP 的接口。
更多配置：  

    - spring.data.elasticsearch.cluster-name Elasticsearch 集群名。(默认值: elasticsearch)
    - spring.data.elasticsearch.cluster-nodes 集群节点地址列表，用逗号分隔。如果没有指定，就启动一个客户端节点。
    - spring.data.elasticsearch.propertie 用来配置客户端的额外属性。
    - spring.data.elasticsearch.repositories.enabled 开启 Elasticsearch 仓库。(默认值:true。)

部分注解介绍：  
#### @Document
@Document注解里面的几个属性，类似Mysql的:
```
index –> DB   
type –> Table   
Document –> row
```
加上@Id注解后，在Elasticsearch里对应的该列就是主键了，在查询时就可以直接用主键查询。其实和mysql非常类似，基本就是一个数据库。  
```
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Document {
    String indexName();//索引库的名称，个人建议以项目的名称命名
    String type() default "";//类型，个人建议以实体的名称命名
    short shards() default 5;//默认分区数
    short replicas() default 1;//每个分区默认的备份数
    String refreshInterval() default "1s";//刷新间隔
    String indexStoreType() default "fs";//索引文件存储类型
}
```
#### @Field  
加上了@Document注解之后，默认情况下这个实体中所有的属性都会被建立索引、并且分词。 

通过@Field注解来进行详细的指定，如果没有特殊需求，那么只需要添加@Document即可。
```
@Field注解的定义如下：  

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Field {

    FieldType type() default FieldType.Auto;#自动检测属性的类型
    FieldIndex index() default FieldIndex.analyzed;#默认情况下分词
    DateFormat format() default DateFormat.none;
    String pattern() default "";
    boolean store() default false;#默认情况下不存储原文
    String searchAnalyzer() default "";#指定字段搜索时使用的分词器
    String indexAnalyzer() default "";#指定字段建立索引时指定的分词器
    String[] ignoreFields() default {};#如果某个字段需要被忽略
    boolean includeInParent() default false;
}
```
#### CURD 操作
同样，类似于jpa，Spring提供了ElasticsearchRepository<T, ID>类，基本的curd都帮我们提供了，我们如果有特别需要只需根据规范命名即可自行扩展，感叹Spring的强大。  
定义自己的Dao  
```
public interface UserSearchRepository extends ElasticsearchRepository<User, Integer> {

    /**
     * Find by name list.
     *
     * @param name the name
     * @return the list
     */
    List<User> findByName(String name);

    /**
     * 使用 Page<User> users = userSearchRepository.findByName("测试",  PageRequest.of(0, 10)); //分页是从0开始的
     *
     * @param name     the name
     * @param pageable the pageable
     * @return the page
     */
    Page<User> findByName(String name, Pageable pageable);

    /**
     * Find product by id user.
     *
     * @param name the name
     * @return the user
     */
    User findProductById(String name);
}
```
model 类定义
```java
@Document(indexName = "springboot-curd", type = "user")
public class User {
    @Id
    private Integer id;

    @Field(searchAnalyzer = "ik_max_word", analyzer = "ik_smart", type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "date_optional_time")
    private Date date;
    
    /* get set */
```
这里的注解概念可基本对应关系型数据库

    indexNmae –> DB   
    type –> Table   
    @Field –> row

Service 实现
```java
@Service
public class UserSearchServiceImpl implements UserSearchService {
    /**
     * The User search repository.
     */
    @Autowired
    UserSearchRepository userSearchRepository;

    private final Logger logger = LoggerFactory.getLogger(UserSearchServiceImpl.class);

    @Override
    public List<User> getUserAll() {
        List<User> userList = new ArrayList<>();
        Iterable<User> userIterable = userSearchRepository.findAll();
        userIterable.forEach(userList::add);
        return userList;
    }

    @Override
    public List<User> getUserLimit(int pageNum, int size) {
        Page<User> userPage = userSearchRepository.findAll(PageRequest.of(pageNum, size));
        return userPage.getContent();
    }

    @Override
    public List<User> getUserByName(String name, int pageNum, int size) {
        Page<User> userPage = userSearchRepository.findByName(name, PageRequest.of(pageNum, size));
        return userPage.getContent();
    }

    @Override
    public List<User> getUserByName(String name) {
        return userSearchRepository.findByName(name);
    }

    @Override
    public Boolean insertUser(User user) {
        try {
            userSearchRepository.save(user);
        } catch (Exception e) {
            logger.warn("添加失败!param:{}", JSONParseUtils.object2JsonString(user), e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean updateUser(User user) {
        try {
            userSearchRepository.save(user);
        } catch (Exception e) {
            logger.warn("修改失败!param:{}", JSONParseUtils.object2JsonString(user), e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean dropUser(Integer id) {
        try {
            userSearchRepository.deleteById(id);
        } catch (Exception e) {
            logger.warn("删除失败!param:{}", id, e);
            return false;
        }
        return true;
    }
}
```
提供API访问  
```java
@RestController
@Api(value = "SpringBoot集成ElasticSearch测试接口", tags = "UserOperationApi")
public class UserOperationApi {

    @Autowired
    private UserSearchService userSearchService;

    @GetMapping("/getAll")
    @ApiOperation(value = "查询全部User信息", notes = "获取全部User信息", response = ApiResult.class)
    public ApiResult getAll() {
        return ApiResult.prepare().success(userSearchService.getUserAll());
    }

    @GetMapping("/getUserByName")
    @ApiOperation(value = "根据用户名查询User信息", notes = "根据用户名查询User信息", response = ApiResult.class)
    @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query")
    public ApiResult getUserByName(String name) {
        return ApiResult.prepare().success(userSearchService.getUserByName(name));
    }

    @GetMapping("/getLimitUser")
    @ApiOperation(value = "根据用户名查询User信息", notes = "根据用户名查询User信息", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "String"),
            @ApiImplicitParam(name = "size", value = "条数", dataType = "String")
    })
    public ApiResult getLimitUser(@RequestParam(defaultValue = "0", required = false) String page,
                                  @RequestParam(defaultValue = "5", required = false) String size) {
        return ApiResult.prepare().success(userSearchService.getUserLimit(
                Integer.parseInt(page), Integer.parseInt(size)));
    }

    @PostMapping("/saveUser")
    @ApiOperation(value = "添加User信息", response = ApiResult.class)
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User")
    public ApiResult saveUser(@RequestBody User user) {
        Boolean isSuccess = userSearchService.insertUser(user);
        if (isSuccess) {
            return ApiResult.prepare().success("添加成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(user), 500, "添加失败!");
    }

    @PostMapping("/updateUser")
    @ApiOperation(value = "修改User信息", response = ApiResult.class)
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User")
    public ApiResult updateUser(@RequestBody User user) {
        Boolean isSuccess = userSearchService.updateUser(user);
        if (isSuccess) {
            return ApiResult.prepare().success("修改成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(user), 500, "添加失败!");
    }

    @GetMapping("/deleteUser")
    @ApiOperation(value = "刪除User信息", response = ApiResult.class)
    @ApiImplicitParam(name = "id", value = "用户Id", required = true, dataType = "Integer")
    public ApiResult updateUser(Integer id) {
        Boolean isSuccess = userSearchService.dropUser(id);
        if (isSuccess) {
            return ApiResult.prepare().success("删除成功!");
        }
        return ApiResult.prepare().error(null, 500, "删除失败!");
    }
}
```

集成Demo可查看源码，地址：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Elasticsearch  

参考链接：  
https://github.com/spring-projects/spring-data-elasticsearch   
https://segmentfault.com/a/1190000015568618#911   
