package com.gousade.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author woxigsd@gmail.com
 * @date 2020-8-25 16:54:43 Description: Swagger3配置类
 * <a href="http://springfox.github.io/springfox/docs/current/#configuration-explained">...</a>
 */
@EnableOpenApi
@Configuration
public class Swagger3Config {

	/*@Value("${swagger.enable}")
	private boolean enable;*/

    @Bean
    public Docket adminApi(Environment environment) {
        Profiles profiles = Profiles.of("dev", "test", "prod");
        boolean enable = environment.acceptsProfiles(profiles);
        return new Docket(DocumentationType.OAS_30).enable(enable).groupName("gousade").apiInfo(apiInfo()).select()
                /**
                 * apis() allows selection of RequestHandler's using a predicate. The example
                 * here uses an any predicate (default). Out of the box predicates provided are
                 * any, none, withClassAnnotation, withMethodAnnotation and basePackage.
                 */
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))//只显示被此注解标注的方法
                .apis(RequestHandlerSelectors.basePackage("com.gousade.controller")).paths(PathSelectors.any()).build();
    }

    @Bean
    public Docket publicApi(Environment environment) {
        Profiles profiles = Profiles.of("dev", "test", "prod");
        boolean enable = environment.acceptsProfiles(profiles);
        return new Docket(DocumentationType.OAS_30).enable(enable).groupName("public").apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("GisardAdminLTE - SpringBoot v1.1.2 API")
                .description("Documentation Description")
                .contact(new Contact("GisardAdminLTE", "https://github.com/woxigousade/gousade", "woxigsd@gmail.com"))
                .version("v1.1.2").license("MIT License")
                .licenseUrl("https://github.com/woxigousade/gousade/blob/master/LICENSE").build();
    }
}