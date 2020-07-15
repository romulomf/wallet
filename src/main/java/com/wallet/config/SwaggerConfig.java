package com.wallet.config;

import java.util.HashMap;
import java.util.Map;

import com.wallet.security.utils.JwtTokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import lombok.NoArgsConstructor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Profile("dev")
@EnableSwagger2
@NoArgsConstructor
public class SwaggerConfig {

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Wallet API").description("Wallet API - Documentação de acesso aos endpoints.").version("1.0").build();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.wallet.controller")).paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}

	@Bean
	@Autowired
	public SecurityConfiguration security(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
		String token = null;
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername("development@swagger.user");
			token = jwtTokenUtil.getToken(userDetails);
		} catch (Exception e) {
			token = "";
		}
		Map<String, Object> params = new HashMap<>(1);
		params.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		SecurityConfigurationBuilder builder = SecurityConfigurationBuilder.builder();
		builder.appName("Wallet API");
		builder.additionalQueryStringParams(params);
		builder.enableCsrfSupport(false);
		builder.scopeSeparator(",");
		return builder.build();
	}
}