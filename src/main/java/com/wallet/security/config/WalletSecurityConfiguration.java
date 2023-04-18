package com.wallet.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wallet.security.JwtAuthenticationEntryPoint;
import com.wallet.security.JwtAuthenticationTokenFilter;

import lombok.NoArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@NoArgsConstructor
public class WalletSecurityConfiguration {

	@Bean
	public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
					.userDetailsService(userDetailsService)
					.passwordEncoder(passwordEncoder)
				.and()
					.build();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, @Autowired JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter,  @Autowired JwtAuthenticationEntryPoint unauthorizedHandler) throws Exception {
		return http
			.csrf(conf -> conf.disable())
			.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
				authorizationManagerRequestMatcherRegistry.antMatchers("/resources/**", "/static/**", "/auth/**", "/user/**", "/h2/**")
					.permitAll().anyRequest().authenticated())
			.headers(headersConfigurer ->
				headersConfigurer.frameOptions(frameOptionsConfig ->
					frameOptionsConfig.sameOrigin().cacheControl(cacheControlConfig -> cacheControlConfig.disable())
				)
			)
			.exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(unauthorizedHandler))
			.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.requestCache(requestCacheConfigurer -> requestCacheConfigurer.disable())
			.securityContext(securityContextConfigurer ->
				securityContextConfigurer.disable().addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class))
			.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.debug(true);
	}
}