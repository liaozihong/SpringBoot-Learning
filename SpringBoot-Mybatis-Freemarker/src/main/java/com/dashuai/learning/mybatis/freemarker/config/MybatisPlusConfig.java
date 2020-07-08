package com.dashuai.learning.mybatis.freemarker.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Mybatis plus config
 * Created in 2020.07.08
 *
 * @author Liaozihong
 * @Description mybatisPlus配置类
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 分页插件
     *
     * @return the pagination interceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * SQL执行效率插件
     *
     * @return the performance interceptor
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}
