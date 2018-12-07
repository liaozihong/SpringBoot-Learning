package com.dashuai.learning.webfluxdata.config;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger config
 * <p/>
 * Created in 2018.11.08
 * <p/>
 * Webflux 暂时不支持 Swagger
 *
 * @author Liaodashuai
 */
//@Configuration
//@EnableSwagger2
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
                .apis(RequestHandlerSelectors.basePackage("com.dashuai.learning.webfluxdata.api"))
                .paths(PathSelectors.any())
                .build();

    }

    private ApiInfo metaData() {

        return new ApiInfoBuilder()
                .title("SpringBoot-WebFlux MongoDb Demo API文档")
                .description("SpringBoot-WebFlux 集成mongoDb 所记录")
                .termsOfServiceUrl("")
                .contact(new Contact("dashuai", "", ""))
                .version("1.0")
                .build();
    }
}
