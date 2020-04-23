### SpringBoot2 集成SpringSecurity资源访问权限限制 

在日常开发中，难免会遇到权限相关的需求，关于权限处理这一块，用的比较多的安全框架分别是Shiro和Spring全家桶中的
Spring-Security，之前已经使用过Shiro，对它也有一定的了解，下面是对Spring-Security学习做的记录。    

#### 概述
Spring Security 是一个能够为基于 Spring 的企业应用系统提供声明式的安全访问控制解决方案的安全框架。它提供了一组可以在 Spring 应用上下文中配置的 Bean，充分利用了Spring IoC，DI（控制反转Inversion of Control ,DI:Dependency Injection 依赖注入）和 AOP（面向切面编程）功能，为应用系统提供声明式的安全访问控制功能，减少了为企业系统安全控制编写大量重复代码的工作。

#### 路由资源访问认证
先用一个比较简单的基于角色访问权限的小例子入门  

导入相关jar包,SpringBoot-Support模块是我常用的一些公共类
```text
    compile project(':SpringBoot-Support')
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-security', version: '2.0.5.RELEASE'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-thymeleaf', version: '2.0.5.RELEASE'
    compile 'org.thymeleaf.extras:thymeleaf-extras-springsecurity4:3.0.2.RELEASE'
```
配置Security 
```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        //直接建两个用户存在内存中，生产环境可以从数据库中读取,对应管理器JdbcUserDetailsManager
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // 创建两个用户
        //通过密码的前缀区分编码方式,推荐,这种加密方式很好的利用了委托者模式，使得程序可以使用多种加密方式，并且会自动
        //根据前缀找到对应的密码编译器处理。
        manager.createUser(User.withUsername("guest").password("{bcrypt}" +
                new BCryptPasswordEncoder().encode("123456")).roles("USER").build());
        manager.createUser(User.withUsername("root").password("{sha256}" +
                new StandardPasswordEncoder().encode("666666"))
                .roles("ADMIN", "USER").build());
        return manager;
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 支持多种编码，通过密码的前缀区分编码方式,推荐
     *
     * @return the password encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:on
        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/fonts/**").permitAll()  // 允许访问资源
                .antMatchers("/", "/home", "/about", "/login").permitAll() //允许访问这三个路由
                .antMatchers("/admin/**").hasAnyRole("ADMIN")   // 满足该条件下的路由需要ROLE_ADMIN的角色
                .antMatchers("/user/**").hasAnyRole("USER")     // 满足该条件下的路由需要ROLE_USER的角色
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).and()
                .csrf().disable();
        // @formatter:off
    }
}
```
上面的代码主要配置了两个用户，分别是root和guest，并分别设置了角色，接着配置静态资源、登录页、首页允许访问，
部分路由指定特定角色可以访问，并配置权限拒绝访问异常处理器和跨越无效。  

权限拒绝访问处理器，配置跳转到特定页面   
```java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication auth
                = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            logger.info("User '" + auth.getName()
                    + "' attempted to access the protected URL: "
                    + httpServletRequest.getRequestURI());
        }

        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/403");
    }
}
```
下面访问，/admin,会自动跳转到/login登录页，使用root账号登录，在访问/admin,会发现访问到了。  
![](http://ww1.sinaimg.cn/large/006mOQRagy1g31yorkhxzj30um0a50tl.jpg)  
接着在注销，使用guest的账号登录，会发现自动跳转到了403页面，也就是访问被拒绝了。  
![](http://ww1.sinaimg.cn/large/006mOQRagy1g31yqg9ku8j30lm08574s.jpg)  
原因是guest只配置Role为USER，而访问/admin需要有ADMIN的角色。  
到这里，简单的通过角色限制资源访问以实现了，后面如果真是有需要要用到，可自行改造。  
比如账号的管理可以从内存移至数据库中等等。  

#### 填坑记录
最后，在记录几处采坑的地方：  
* Security的配置中，有个坑，需要自己配置passwordEncoder，否则，
   登录会报java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"。  
   简单的了解下PasswordEncoder:  
   根据官方的定义,Spring Security的PasswordEncoder接口用于执行密码的单向转换，以便安全地存储密码。
   而DelegatingPasswordEncoder和NoOpPasswordEncoder都是PasswordEncoder接口的实现类,简单来说就是现在数据库存储的密码基本都是经过编码的,而决定如何编码以及判断未编码的字符序列和编码后的字符串是否匹配就是PassswordEncoder的责任.  
   随着安全要求的提高,NoOpPasswordEncoder已经不被推荐了，如果你未自行加密，那么他存储的将是明文，并且当你因为加密算法安全性不够，需要更改时，将会变得非常棘手.    
   而DelegatingPasswordEncoder，他是一个委派密码编译器，会根据不同的加密算法前缀标识委派给不同的密码编译器处理，
   使得程序可以同时存在好几种加密算法,所以这里推荐配置使用DelegatingPasswordEncoder，这里仅仅是提及一下，若想深入了解PassEncoder可在自行Google。  
```java
    /**
     * 支持多种编码，通过密码的前缀区分编码方式,推荐
     * 对应的密码需加上加密算法标识，如:{bcrypt}、{MD5}..........
     * @return the password encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
```
* 模板页面标签不生效，sec:authentication，原因可能如下:  
thymeleaf-extras-springsecurity4 Jar包未导入，或者版本与Security版本不匹配  
官方描述如下：  
```text
This is a thymeleaf extras module, not a part of the Thymeleaf core (and as such following its own versioning schema), but fully supported by the Thymeleaf team.
This repository contains 3 projects:
thymeleaf-extras-springsecurity3 for integration with Spring Security 3.x
thymeleaf-extras-springsecurity4 for integration with Spring Security 4.x
thymeleaf-extras-springsecurity5 for integration with Spring Security 5.x
Current versions:
Version 3.0.4.RELEASE - for Thymeleaf 3.0 (requires Thymeleaf 3.0.10+)
Version 2.1.3.RELEASE - for Thymeleaf 2.1 (requires Thymeleaf 2.1.2+)
```
按官方描述，我用的是Spring Security5.X的包，应该使用thymeleaf-extras-springsecurity5的包，但它确不生效。  
无奈之下尝试使用thymeleaf-extras-springsecurity4的包，便可以了,这里建议5.0.X的还是用
thymeleaf-extras-springsecurity4的包，毕竟亲测生效了。  

项目源码地址：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-SpringSecurity


参考链接：  
https://docs.spring.io/spring-security/site/docs/5.2.0.BUILD-SNAPSHOT/reference/htmlsingle/
 https://blog.csdn.net/alinyua/article/details/80219500  
 https://juejin.im/entry/5a45fe8e6fb9a045132b0658  
 https://segmentfault.com/a/1190000015191298  