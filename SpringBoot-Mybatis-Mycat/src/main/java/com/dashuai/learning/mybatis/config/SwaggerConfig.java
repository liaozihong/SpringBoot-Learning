package com.dashuai.learning.mybatis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger config
 * <p/>
 * Created in 2018.11.08
 * <p/>
 *
 * @author Liaodashuai
 */
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
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metaData())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dashuai.learning.mybatis.api"))
                .paths(PathSelectors.any())
                .build();

    }

    private ApiInfo metaData() {

        return new ApiInfoBuilder()
                .title("API文档")
                .description("描述")
                .termsOfServiceUrl("")
                .contact(new Contact("dashuai", "", ""))
                .version("1.0")
                .build();
    }
}
