## SpringBoot 集成 Mybatis实现双数据源
Mybatis 常用ORM工具，不过多介绍，与SpringBoot集成实现单数据源可参考：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Mybatis  
导入jar包，使用druid连接池
```gradle
compile group: 'com.alibaba', name: 'druid', version: '1.1.10'
compile group: 'mysql', name: 'mysql-connector-java', version: '5.1.46'
compile group: 'org.mybatis.spring.boot', name: 'mybatis-spring-boot-starter', version: '1.3.2'
```
## 前景
项目需要用到两个数据库，也就是双数据源Spring数据库和Test数据库，Mybatis的配置如下：  
## 配置示例
**配置Mybatis，配置两个DataSource数据源，并表示Bean名**
```java
@Configuration
public class MybatisConfiguration {
    /**
     * spring数据库配置前缀.
     */
    final static String SPRING_PREFIX = "spring.datasource.spring";
    /**
     * test数据库配置前缀.
     */
    final static String TEST_PREFIX = "spring.datasource.test";

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
     * Spring data source data source.
     *
     * @return the data source
     */
    @Bean(name = "SpringDataSource")
    @ConfigurationProperties(prefix = SPRING_PREFIX)  // application.properties中对应属性的前缀
    public DataSource springDataSource() {
        return new DruidDataSource();
    }

    /**
     * Test data source data source.
     *
     * @return the data source
     */
    @Bean(name = "TestDataSource")
    @ConfigurationProperties(prefix = TEST_PREFIX)  // application.properties中对应属性的前缀
    public DataSource testDataSource() {
        return new DruidDataSource();
    }
}
```  
**配置Spring数据库的数据源**
```java
/**
 * 注意basePackages 指定扫描Mapper的包路径，sqlSessionFactoryRef 指定sql session工厂，跟下面的方法名相等
 */
@Configuration
@MapperScan(basePackages = {"com.dashaui.learning.multisource.mapper.spring"}, sqlSessionFactoryRef = "springSqlSessionFactory")
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
**配置Test数据库的数据源**
```java
@Configuration
@MapperScan(basePackages = {"com.dashaui.learning.multisource.mapper.test"}, sqlSessionFactoryRef = "testSqlSessionFactory")
public class TestDataSourceConfiguration {
    /**
     * The constant MAPPER_XML_LOCATION.
     */
    public static final String MAPPER_XML_LOCATION = "classpath:mapper/test/*.xml";

    /**
     * The Open plat form data source.
     */
    @Autowired
    @Qualifier("TestDataSource")
    DataSource testDataSource;

    /**
     * Open plat form sql session template sql session template.
     *
     * @return the sql session template
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionTemplate testSqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(testSqlSessionFactory());
    }

    /**
     * Open plat form sql session factory sql session factory.
     *
     * @return the sql session factory
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionFactory testSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(testDataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_LOCATION));
        return factoryBean.getObject();
    }
}
```
配置成功即可。配置注意扫描包的路径和DataSource的name，不要混了。个人实验没问题。  
Github地址:https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Mybatis-Multisource
