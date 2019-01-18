package com.dashuai.learning.jta.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * Spring data source configuration
 * <p/>
 * Created in 2018.12.03
 * <p/>
 * 注意basePackages 指定扫描Mapper的包路径，sqlSessionFactoryRef 指定sql session工厂，跟下面的方法名相等
 *
 * @author Liaozihong
 */
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
