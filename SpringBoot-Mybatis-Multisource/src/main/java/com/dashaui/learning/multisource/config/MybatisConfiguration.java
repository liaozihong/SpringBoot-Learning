package com.dashaui.learning.multisource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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

    final static Logger logger = LoggerFactory.getLogger(MybatisConfiguration.class);

    /**
     * 配置druid显示监控统计信息
     * 开启Druid的监控平台 http://localhost:8080/druid
     *
     * @return
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
     * @return
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

    @Bean(name = "SpringDataSource")
    @ConfigurationProperties(prefix = SPRING_PREFIX)  // application.properties中对应属性的前缀
    public DataSource springDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "TestDataSource")
    @ConfigurationProperties(prefix = TEST_PREFIX)  // application.properties中对应属性的前缀
    public DataSource testDataSource() {
        return new DruidDataSource();
    }
}
