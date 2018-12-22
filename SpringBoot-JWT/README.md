## 什么是JWT?
JSON Web Token（JWT）是一个开放标准（RFC 7519），它定义了一种紧凑且独立的方式，可以在各方之间作为JSON对象安全地传输信息。此信息可以通过数字签名进行验证和信任。JWT可以使用秘密（使用HMAC算法）或使用RSA或ECDSA的公钥/私钥对进行签名。  
虽然JWT可以加密以在各方之间提供保密，但我们将专注于签名令牌。签名令牌可以验证其中包含的声明的完整性，而加密令牌则隐藏其他方的声明。当使用公钥/私钥对签署令牌时，签名还证明只有持有私钥的一方是签署私钥的一方。  

### 使用场景
特别适用于分布式站点的单点登录（SSO）场景。JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息， 以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该token也可直接被用于认证，也可被加密。  
JWT是由三段信息构成的，将这三段信息文本用.链接一起就构成了Jwt字符串。
格式如下：  
```
xxxxx.yyyyy.zzzzz
```
就像这样:   
```text
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```
### JWT的构成  
第一部分我们称它为头部（header),第二部分我们称其为载荷（payload, 类似于飞机上承载的物品)，第三部分是签证（signature)。  

#### header 头部

标头通常由两部分组成：令牌的类型，即JWT，以及正在使用的签名算法，例如HMAC SHA256或RSA。  

这里的加密算法是单向函数散列算法，常见的有MD5、SHA、HAMC。这里使用基于密钥的Hash算法HMAC生成散列值。  

MD5 message-digest algorithm 5 （信息-摘要算法）缩写，广泛用于加密和解密技术，常用于文件校验。校验？不管文件多大，经过MD5后都能生成唯一的MD5值
SHA (Secure Hash Algorithm，安全散列算法），数字签名等密码学应用中重要的工具，安全性高于MD5。  

HMAC (Hash Message Authentication Code，散列消息鉴别码，基于密钥的Hash算法的认证协议。用公开函数和密钥产生一个固定长度的值作为认证标识，用这个标识鉴别消息的完整性。常用于接口签名验证
完整的头部就像下面这样的JSON：
```json
{
  'typ': 'JWT',
  'alg': 'HS256'
}
```
然后将头部进行base64加密（该加密是可以对称解密的),构成了第一部分
```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9
playload
```
#### payload 载荷
令牌的第二部分是有效负载，其中包含声明。声明是关于实体（通常是用户）和其他数据的声明。声明有三种类型：注册，公开和私人。
载荷就是存放有效信息的地方，这些有效信息包含三个部分：

标准中注册的声明
公共的声明
私有的声明

标准中注册的声明 (建议但不强制使用) ：
```
iss: jwt签发者
sub: jwt所面向的用户
aud: 接收jwt的一方
exp: jwt的过期时间，这个过期时间必须要大于签发时间
nbf: 定义在什么时间之前，该jwt都是不可用的.
iat: jwt的签发时间
jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
公共的声明：
```
公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息.但不建议添加敏感信息，因为该部分在客户端可解密  

私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为base64是对称解密的，意味着该部分信息可以归类为明文信息。  

定义一个payload:
```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```
然后将其进行base64加密，得到Jwt的第二部分：  
```
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9
```
请注意，对于签名令牌，此信息虽然可以防止被篡改，但任何人都可以读取。除非加密，否则不要将秘密信息放在JWT的有效负载或头元素中。  

#### signature 签名
要创建签名部分，您必须采用编码标头，编码的有效负载，盐值，标头中指定的算法，并对其进行签名。  
这个签证信息由三部分组成：  

    header (base64后的)  
    payload (base64后的)  
    secret  
这个部分需要base64加密后的header和base64加密后的payload使用.连接组成的字符串， 然后通过header中声明的加密方式进行加盐secret组合加密，然后就构成了jwt的第三部分。   
例如，如果要使用HMAC SHA256算法，将按以下方式创建签名：

    HMACSHA256(
      base64UrlEncode(header) + "." +
      base64UrlEncode(payload),
      secret)

javascript例子如下：  
```javascript
// javascript
var encodedString = base64UrlEncode(header) + '.' + base64UrlEncode(payload);
var signature = HMACSHA256(encodedString, 'secret'); // TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```
将这三部分用.连接成一个完整的字符串,构成了最终的jwt:  
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```
注意：secret是保存在服务器端的，jwt的签发生成也是在服务器端的，secret就是用来进行jwt的签发和jwt的验证， 所以，它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。  

### 与SpringBoot shiro集成
1. 导入所需jar包
```
 compile 'com.auth0:java-jwt:3.4.0'
compile 'org.apache.shiro:shiro-spring:1.4.0'
compile 'org.springframework.boot:spring-boot-starter-aop:2.0.4.RELEASE'
compile group: 'com.alibaba', name: 'druid', version: '1.1.10'
compile group: 'mysql', name: 'mysql-connector-java', version: '5.1.46'
compile group: 'org.mybatis.spring.boot', name: 'mybatis-spring-boot-starter', version: '1.3.2'
compile group: 'org.springframework.boot', name: 'spring-boot-devtools', version: '2.0.4.RELEASE'
```
> 使用Mybatis+Shiro做权限验证，这里有一个坑要注意下，AOP jar包一定要导入，不然验证权限注解将失效。  
 导致不会进入doGetAuthorizationInfo()方法。  

2. 实现JWT 请求验证
主要思路
> 首先用户登录成功后，利用官方的JWT包，配置生成并返回一段token，接着配置JWT的检验token过滤器，让请求都需要验证是否加上此token在请求头上。  
没有则会跳到无授权。  

利用JWT包，构造生成TOKEN和检验token的方法。  
```java
public class JWTUtil {
    /**
     * 过期时间 5 分钟
     */
    private static final long EXPIRE_TIME = 60 * 1000 * 5;
    /**
     * 密钥，注意这里如果真实用到，应当设置到复杂点，相当于私钥的存在。如果被人拿到，想到于它可以自己制造token了。
     */
    private static final String SECRET = "LIAODASHUAI";

    /**
     * 生成 token, 5min后过期
     *
     * @param username 用户名
     * @return 加密的token string
     */
    public static String createToken(String username) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                //到期时间
                .withExpiresAt(date)
                //创建一个新的JWT，并使用给定的算法进行标记
                .sign(algorithm);
    }

    /**
     * 校验 token 是否正确
     *
     * @param token    密钥
     * @param username 用户名
     * @return 是否正确 boolean
     */
    public static boolean verify(String token, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            //在token中附带了username信息
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            //验证 token
            verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @param token the token
     * @return token中包含的用户名 username
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
```
重新实现AuthenticationToken类，让其存放token，便于校验。  
```java
public class JWTToken implements AuthenticationToken {
    private String token;

    /**
     * Instantiates a new Jwt token.
     *
     * @param token the token
     */
    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
```
接着到了关键的JWT过滤器实现，此过滤器继承实现了BasicHttpAuthenticationFilter的部分方法。  
主要作用是： 

    检验请求头是否带有 token,req.getHeader("token")!=null
    如果带有 token，执行 shiro 的 login() 方法，将 token 提交到 Realm 中进行检验；如果没有 token，说明当前状态为游客状态（或者其他一些不需要进行认证的接口）
    如果在 token 校验的过程中出现错误，如 token 校验失败，那么我会将该请求视为认证不通过，则重定向到 /unauthorized/**
```java
public class JWTFilter extends BasicHttpAuthenticationFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 如果带有 token，则对 token 进行检查，否则直接通过
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws UnauthorizedException {
        //判断请求的请求头是否带上 "token"
        if (isLoginAttempt(request, response)) {
            //如果存在，则进入 executeLogin 方法执行登入，检查 token 是否正确
            try {
                executeLogin(request, response);
                return true;
            } catch (Exception e) {
                //token 错误
                responseError(response, e.getMessage());
            }
        }
        //如果请求头不存在 Token，则可能是执行登陆操作或者是游客状态访问，无需检查 token，直接返回 true
        return true;
    }

    /**
     * 判断用户是否想要登入。
     * 检测 header 里面是否包含 Token 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("token");
        return token != null;
    }

    /**
     * 执行登陆操作
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");
        JWTToken jwtToken = new JWTToken(token);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 将非法请求跳转到 /unauthorized/**
     */
    private void responseError(ServletResponse response, String message) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            //设置编码，否则中文字符在重定向时会变为空字符串
            message = URLEncoder.encode(message, "UTF-8");
            httpServletResponse.sendRedirect("/unauthorized/" + message);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
```
继承AuthorizingRealm，实现用户授权的验证和权限的验证
```java
public class CustomRealm extends AuthorizingRealm {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    RoleMapper roleMapper;

    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("————身份认证方法————");
        String token = (String) authenticationToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtil.getUsername(token);
        if (username == null || !JWTUtil.verify(token, username)) {
            throw new AuthenticationException("token认证失败！");
        }
        UserInfo userInfo = userInfoMapper.selectByName(username);
        if (userInfo == null) {
            throw new AuthenticationException("该用户不存在！");
        }
        if (userInfo.getState() == 1) {
            throw new AuthenticationException("该用户已被封号！");
        }
        return new SimpleAuthenticationInfo(token, token, "MyRealm");
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("————权限认证————");
        String username = JWTUtil.getUsername(principals.toString());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 此处最好使用缓存提升速度
        UserInfo userInfo = userInfoMapper.selectByName(username);
        userInfo = userInfoMapper.selectUserOfRole(userInfo.getUid());
        if (userInfo == null || userInfo.getRoleList().isEmpty()) {
            return authorizationInfo;
        }
        for (Role role : userInfo.getRoleList()) {
            authorizationInfo.addRole(role.getRole());
            role = roleMapper.selectRoleOfPerm(role.getId());
            if (role == null || role.getPermissions().isEmpty()) {
                continue;
            }
            for (Permission p : role.getPermissions()) {
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }
}
```
配置ShiroConfig，将自定义的过滤器设置进去  
```java
@Configuration
public class ShiroConfig {
    /**
     * 先走 filter ，然后 filter 如果检测到请求头存在 token，则用 token 去 login，走 Realm 去验证
     *
     * @param securityManager the security manager
     * @return the shiro filter factory bean
     */
    @Bean
    public ShiroFilterFactoryBean factory(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        Map<String, Filter> filterMap = new HashMap<>();
        //设置我们自定义的JWT过滤器
        filterMap.put("jwt", new JWTFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(securityManager);
        // 设置无权限时跳转的 url;
        factoryBean.setUnauthorizedUrl("/unauthorized/无权限");
        Map<String, String> filterRuleMap = new HashMap<>();
        //访问/login和/unauthorized 不需要经过过滤器
        filterRuleMap.put("/login", "anon");
        filterRuleMap.put("/unauthorized/**", "anon");
        // 所有请求通过我们自己的JWT Filter
        filterRuleMap.put("/**", "jwt");
        // 访问 /unauthorized/** 不通过JWTFilter
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    /**
     * 注入 securityManager
     *
     * @return the security manager
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置自定义 realm.
        securityManager.setRealm(customRealm());
        /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    @Bean
    public CustomRealm customRealm() {
        return new CustomRealm();
    }

    /**
     * 开启shiro aop注解支持. 使用代理方式; 所以需要开启代码支持;
     *
     * @param securityManager 安全管理器
     * @return 授权Advisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
```
配值好后，接入swagger2,方便测试接口,配置swagger时，设置一个header参数的token,方便我们调用。  
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Create rest api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket createRestApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        //header中的token参数非必填，传空也可以
        tokenPar.name("token").description("请求接口所需Token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metaData())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dashuai.learning.jwt.api"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("集成JWT API文档")
                .description("描述")
                .termsOfServiceUrl("")
                .contact(new Contact("dashuai", "https://github.com/liaozihong", "15017263266@173.com"))
                .version("1.0")
                .build();
    }
}
```
调用授权api，登录成功，会返回token:   
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyfnrtqb5kj315u0q5dhx.jpg)    
拿到返回的token，调用接口，可以看到成功调用：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyfntf2bhuj31aw0n8764.jpg)   
去掉token或使用错误的token将会报token认证失败：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyfnuknvrkj310a0lz0u1.jpg)  

JWT 弊端，如果使用JWT来着做会话管理，那么注销、改密、续签等问题，你将要慢慢爬坑。
具体可参考大佬写的一篇文章：http://blog.didispace.com/learn-how-to-use-jwt-xjf/

Demo源码：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-JWT.git  

参考链接：  
[JWT官方链接](https://jwt.io/introduction/)   
[使用JWt带来的一些问题](http://blog.didispace.com/learn-how-to-use-jwt-xjf/)
https://www.xncoding.com/2017/07/09/spring/sb-jwt.html     