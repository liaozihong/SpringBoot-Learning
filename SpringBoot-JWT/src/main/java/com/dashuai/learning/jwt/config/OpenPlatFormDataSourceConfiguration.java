package com.dashuai.learning.jwt.config;

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
 * Open plat form data source configuration
 * <p/>
 * Created in 2018.10.29
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
@MapperScan(basePackages = {"com.dashuai.learning.jwt.mapper"}, sqlSessionFactoryRef = "openPlatFormSqlSessionFactory")
public class OpenPlatFormDataSourceConfiguration {
    /**
     * The constant MAPPER_XML_LOCATION.
     */
    public static final String MAPPER_XML_LOCATION = "classpath:mapper/*.xml";

    /**
     * The Open plat form data source.
     */
    @Autowired
    @Qualifier("openPlatFormDataSource")
    DataSource openPlatFormDataSource;

    /**
     * Open plat form sql session template sql session template.
     *
     * @return the sql session template
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionTemplate openPlatFormSqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(openPlatFormSqlSessionFactory());
    }

    /**
     * Open plat form sql session factory sql session factory.
     *
     * @return the sql session factory
     * @throws Exception the exception
     */
    @Bean
    public SqlSessionFactory openPlatFormSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(openPlatFormDataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_LOCATION));
        return factoryBean.getObject();
    }
}
