package com.wallet.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.CacheControlConfig;
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
@EnableMethodSecurity(prePostEnabled = true)
@NoArgsConstructor
public class WalletSecurityConfiguration {

	@Bean
	AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
		AuthenticationManagerBuilder authManager = http.getSharedObject(AuthenticationManagerBuilder.class);
		authManager.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		return authManager.build();
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, @Autowired JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, @Autowired JwtAuthenticationEntryPoint unauthorizedHandler) throws Exception {
		return http
			.csrf(conf -> conf.disable())
			.authorizeHttpRequests(ahr -> ahr
				.requestMatchers("/resources/**", "/static/**", "/auth/**", "/user/**", "/h2/**")
				.permitAll().anyRequest().authenticated()
			)
			.headers(hc -> hc.frameOptions(foc -> foc.sameOrigin().cacheControl(CacheControlConfig::disable)))
			.exceptionHandling(ehc -> ehc.authenticationEntryPoint(unauthorizedHandler))
			.sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.requestCache(AbstractHttpConfigurer::disable)
			.securityContext(scc -> scc.disable().addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class))
			.build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.debug(true);
	}
}