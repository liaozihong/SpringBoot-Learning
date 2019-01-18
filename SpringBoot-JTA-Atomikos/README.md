## SpringBoot2.X 集成Atomikos、Druid 实现多数据源及对分布式事务的应用

### 使用JTA处理分布式事务
Spring Boot通过Atomkos或Bitronix的内嵌事务管理器支持跨多个XA资源的分布式JTA事务，当部署到恰当的J2EE应用服务器时也会支持JTA事务。  

当发现JTA环境时，Spring Boot将使用Spring的 JtaTransactionManager 来管理事务。自动配置的JMS，DataSource和JPA　beans将被升级以支持XA事务。可以使用标准的Spring idioms，比如 @Transactional ，来参与到一个分布式事务中。如果处于JTA环境，但仍想使用本地事务，你可以将 spring.jta.enabled 属性设置为 false 来禁用JTA自动配置功能。  

> 常用的事务管理器有：Atomikos、Bitronix、Narayana。   
本文主要围绕Atomikos展开，另外的常用事务管理器可执行搜索了解。  

### Atomikos简介
Atomikos是一种流行的开源事务管理器，可以嵌入到Spring Boot应用程序中，你可以使用spring-boot-starter-jta-atomikos启动器来拉取适当的Atomikos库，Spring Boot可以自动配置Atomikos，并确保将适当的依赖设置应用到你的Spring bean中，以实现正确的启动和关闭顺序。  

默认情况下，Atomikos事务日志被写入应用程序的主目录中的transaction-logs目录(应用程序jar文件所在的目录)，你可以通过在application.properties文件中设置spring.jta.log-dir来定制这个目录的位置，从spring.jta.atomikos.properties开始的属性还可以用于定制Atomikos UserTransactionServiceImp，
请参阅 [AtomikosProperties Javadoc](https://docs.spring.io/spring-boot/docs/2.0.4.RELEASE/api/org/springframework/boot/jta/atomikos/AtomikosProperties.html) 获取完整的详细信息。  

> 为了确保多个事务管理器可以安全地协调相同的资源管理器，每个Atomikos实例必须配置唯一的ID，默认情况下，这个ID是Atomikos运行的机器的IP地址。为了确保生产中具有唯一性，你应该为应用程序的每个实例配置spring.jta.transaction-manager-id属性的不同值。

### 与SpringBoot集成
SpringBoot默认提供了配置依赖，可直接导入jar包。  
```
compile group: 'org.springframework.boot', name: 'spring-boot-starter-jta-atomikos', version: '2.0.4.RELEASE'
compile group: 'com.alibaba', name: 'druid', version: '1.1.10'
compile group: 'mysql', name: 'mysql-connector-java', version: '5.1.46'
compile group: 'org.mybatis.spring.boot', name: 'mybatis-spring-boot-starter', version: '1.3.2'
testCompile group: 'org.springframework.boot', name: 'spring-boot-starter-test', version: '2.0.4.RELEASE'
```
### 配置多数据源
配置atomikos事务管理器，并配置druid作为数据源并且进行监控   
注入数据源使用使用的是com.atomikos.jdbc.AtomikosDataSourceBean，所以参照此类，可以制定以下配置，再使用
@ConfigurationProperties注解根据前缀将配置注入该datasource,省取繁琐的设置配置。 
```properties
##Spring表数据库配置
spring.jta.atomikos.datasource.spring.max-pool-size=25
spring.jta.atomikos.datasource.spring.min-pool-size=3
spring.jta.atomikos.datasource.spring.max-lifetime=20000
spring.jta.atomikos.datasource.spring.borrow-connection-timeout=10000
spring.jta.atomikos.datasource.spring.unique-resource-name=spring
spring.jta.atomikos.datasource.spring.xa-properties.url=jdbc:mysql://127.0.0.1:3306/spring?useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=UTC
spring.jta.atomikos.datasource.spring.xa-properties.username=root
spring.jta.atomikos.datasource.spring.xa-properties.password=root
spring.jta.atomikos.datasource.spring.xa-properties.driverClassName=com.mysql.jdbc.Driver
# 初始化大小，最小，最大
spring.jta.atomikos.datasource.spring.xa-properties.initialSize=10
spring.jta.atomikos.datasource.spring.xa-properties.minIdle=20
spring.jta.atomikos.datasource.spring.xa-properties.maxActive=100
## 配置获取连接等待超时的时间
spring.jta.atomikos.datasource.spring.xa-properties.maxWait=60000
# 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
spring.jta.atomikos.datasource.spring.xa-properties.timeBetweenEvictionRunsMillis=60000
# 配置一个连接在池中最小生存的时间，单位是毫秒
spring.jta.atomikos.datasource.spring.xa-properties.minEvictableIdleTimeMillis=300000
spring.jta.atomikos.datasource.spring.xa-properties.testWhileIdle=true
spring.jta.atomikos.datasource.spring.xa-properties.testOnBorrow=false
spring.jta.atomikos.datasource.spring.xa-properties.testOnReturn=false
# 打开PSCache，并且指定每个连接上PSCache的大小
spring.jta.atomikos.datasource.spring.xa-properties.poolPreparedStatements=true
spring.jta.atomikos.datasource.spring.xa-properties.maxPoolPreparedStatementPerConnectionSize=20
# 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
spring.jta.atomikos.datasource.spring.xa-properties.filters=stat,slf4j,wall
spring.jta.atomikos.datasource.spring.xa-data-source-class-name=com.alibaba.druid.pool.xa.DruidXADataSource

#------------------------------ 分隔符-------------------------------------
##test表数据库配置
spring.jta.atomikos.datasource.test.max-pool-size=25
spring.jta.atomikos.datasource.test.min-pool-size=3
spring.jta.atomikos.datasource.test.max-lifetime=20000
spring.jta.atomikos.datasource.test.borrow-connection-timeout=10000
spring.jta.atomikos.datasource.test.unique-resource-name=test
spring.jta.atomikos.datasource.test.xa-properties.url=jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=UTC
spring.jta.atomikos.datasource.test.xa-properties.username=root
spring.jta.atomikos.datasource.test.xa-properties.password=root
spring.jta.atomikos.datasource.test.xa-properties.driverClassName=com.mysql.jdbc.Driver
spring.jta.atomikos.datasource.test.xa-properties.initialSize=10
spring.jta.atomikos.datasource.test.xa-properties.minIdle=20
spring.jta.atomikos.datasource.test.xa-properties.maxActive=100
spring.jta.atomikos.datasource.test.xa-properties.maxWait=60000
spring.jta.atomikos.datasource.test.xa-properties.timeBetweenEvictionRunsMillis=60000
spring.jta.atomikos.datasource.test.xa-properties.minEvictableIdleTimeMillis=300000
spring.jta.atomikos.datasource.test.xa-properties.testWhileIdle=true
spring.jta.atomikos.datasource.test.xa-properties.testOnBorrow=false
spring.jta.atomikos.datasource.test.xa-properties.testOnReturn=false
spring.jta.atomikos.datasource.test.xa-properties.poolPreparedStatements=true
spring.jta.atomikos.datasource.test.xa-properties.maxPoolPreparedStatementPerConnectionSize=20
# 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
spring.jta.atomikos.datasource.test.xa-properties.filters=stat,slf4j,wall
spring.jta.atomikos.datasource.test.xa-data-source-class-name=com.alibaba.druid.pool.xa.DruidXADataSource
```
接着将配置注入数据源,并且设置durid监控中心：  
```java
@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement(proxyTargetClass = true)
public class MybatisConfiguration {
    /**
     * spring数据库配置前缀.
     */
    final static String SPRING_PREFIX = "spring.jta.atomikos.datasource.spring";
    /**
     * test数据库配置前缀.
     */
    final static String TEST_PREFIX = "spring.jta.atomikos.datasource.test";

    /**
     * The constant logger.
     */
    final static Logger logger = LoggerFactory.getLogger(MybatisConfiguration.class);

    /**
     * 配置druid显示监控统计信息
     * 开启Druid的监控平台 http://localhost:8080/druid
     *
     * @return servlet registration bean
     */
    @Bean
    public ServletRegistrationBean druidServlet() {
        logger.info("Init Druid Servlet Configuration ");
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // IP白名单，不设默认都可以
//        servletRegistrationBean.addInitParameter("allow", "192.168.2.25,127.0.0.1");
        // IP黑名单(共同存在时，deny优先于allow)
        servletRegistrationBean.addInitParameter("deny", "192.168.1.100");
        //控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", "root");
        servletRegistrationBean.addInitParameter("loginPassword", "dashuai");
        //是否能够重置数据 禁用HTML页面上的“Reset All”功能
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个filterRegistrationBean
     *
     * @return filter registration bean
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    /**
     * 配置Spring数据库的数据源
     *
     * @return the data source
     */
    @Bean(name = "SpringDataSource")
    @ConfigurationProperties(prefix = SPRING_PREFIX)  // application.properties中对应属性的前缀
    public DataSource springDataSource() {
        return new AtomikosDataSourceBean();
    }

    /**
     * 配置Test数据库的数据源
     *
     * @return the data source
     */
    @Bean(name = "TestDataSource")
    @ConfigurationProperties(prefix = TEST_PREFIX)  // application.properties中对应属性的前缀
    public DataSource testDataSource() {
        return new AtomikosDataSourceBean();
    }
}
```
再分别对每个数据源进行sessionfactory的配置：  
```java
@Configuration
@MapperScan(basePackages = {"com.dashuai.learning.jta.mapper.spring"}, sqlSessionFactoryRef = "springSqlSessionFactory")
public class SpringDataSourceConfiguration {
    /**
     * The constant MAPPER_XML_LOCATION.
     */
    public static final String MAPPER_XML_LOCATION = "classpath:mapper/spring/*.xml";

    /**
     * The Open plat form data source.
     */
    @Autowired
    @Qualifier("SpringDataSource")
    DataSource springDataSource;

    /**
     * 配置Sql Session模板
     *
     * @return the sql session template
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionTemplate springSqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(springSqlSessionFactory());
    }

    /**
     * 配置SQL Session工厂
     *
     * @return the sql session factory
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionFactory springSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(springDataSource);
        //指定XML文件路径
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_LOCATION));
        return factoryBean.getObject();
    }
}
```
Test数据源与Spring数据源大同小异，详情可查看源码。  
配置到达这里就完成了。再写一个测试用例,测试多数据源和事务效果：  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class JtaApplicationTests {

    @Autowired
    UserService userService;
    @Autowired
    PeopleService peopleService;

    @Test
    @Transactional
    public void contextLoads() {
        User user=new User();
        user.setUserName("你妹哦");
        user.setPassword("我去");
        user.setAge(20);
        userService.insertUser(user);
        People people = new People();
        people.setName("你大爺的");
        people.setAge(50);
        people.setSex("男");
        peopleService.insertPeople(people);
    }
}
```
由于是测试用例，默认@Transactional在全部成功执行完成会回滚，经测试没问题。  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fzas8yi4ezj319p08habz.jpg)  
再写一个接口对两个表进行添加操作，并且其中一条语句执行失败，查看回滚效果：  
```java
    @Override
    @Transactional
    public Boolean insertUserAndPeople(User user, People people) throws RuntimeException {
        peopleMapper.insert(people);
        try {
            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new RuntimeException("抛出runtime异常，导致回滚数据");
        }
        return true;
    }
```
```java
    @PostMapping(value = "/insertPeopleAndUser", produces = "application/json;charset=UTF-8")
    @ApiOperation(value = "添加两个表", notes = "测试分布式事务", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "peopleName", value = "人名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userName", value = "用户信息", required = true, dataType = "String")
    })
    public ApiResult insertPeopleAndUser(String peopleName,String userName) throws Exception {
        User user=new User();
        user.setUserName(userName);
        user.setPassword("15251251");
        user.setAge(22);
        People people = new People();
        people.setName(peopleName);
        people.setAge(20);
        people.setSex("男");
        Boolean isSuccess = peopleService.insertUserAndPeople(user, people);
        if (isSuccess) {
            return ApiResult.prepare().success("同时添加两表成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(people), 500, "添加失败，全部回滚");
    }
```
由于User表的name字段设置了唯一，所以只需插入重复即会报错。  

> 注意，如果@Tranactional失效，可以思考以下问题：
1.mysql是否使用的是innodb；
2.Spring AOP只在抛出RuntimeException时才回滚，就是需要在捕获异常的最后加上throw new RuntimeException;
3.尝试手动回滚。给注解加上参数如：@Transactional(rollbackFor=Exception.class)

分别向spring库里的user表和test库里的tet表插入一条数据  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fzarp6ks5hj308v03sglk.jpg)    
![](https://ws1.sinaimg.cn/large/006mOQRagy1fzarqircf9j30ez03ajre.jpg)    
接着，返回接口添加user表已有的name，触发异常，查看回滚效果。  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fzart26t4gj31a10p740s.jpg)   
出现唯一索引异常  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fzarskw0k1j311s03y74y.jpg)  
查看表插入情况：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fzaru8ip89j30f4042dfv.jpg)  
可以看到，people表并没有插入数据，也就是当出现异常时，全部数据都回滚了。  

> 学习之路，本就如同逆流而上，不进即退。加油!

本例子的源码已上传至github：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-JTA-Atomikos.git   

参考链接：  
[Spring boot atomikos 配置好后 @Transactional 注解不生效](https://blog.csdn.net/qq_32447301/article/details/81235582)
[Spring Boot(二三) - 使用JTA处理分布式事务](https://www.hifreud.com/2017/07/12/spring-boot-23-jta-handle-distribute-transaction/#%E4%BD%BF%E7%94%A8jta%E5%A4%84%E7%90%86%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1)  



