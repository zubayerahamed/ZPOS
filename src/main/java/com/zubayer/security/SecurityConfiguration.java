package com.zubayer.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.zubayer.service.impl.UsersServiceImpl;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired private UsersServiceImpl userService;

	private static final String[] ALLOWED_URLS = new String[]{
		"/login/**", 
		"/assets/**",
		"/register/**",
		"/business/**",
		"/clearlogincache",
		"/api/v1/**",
	};

	@Bean
	AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).build();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
		http
			.addFilter(authenticationFilter(authenticationManager))
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(ALLOWED_URLS).permitAll()
				.anyRequest().authenticated()
			)
			.authenticationProvider(authProvider())
			.formLogin(formLogin -> formLogin
				.loginPage("/login")
				.failureUrl("/login?error")
				.permitAll()
				.defaultSuccessUrl("/")
			)
			.logout(logout -> logout 
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			)
			.exceptionHandling(exc -> exc.accessDeniedPage("/accessdenied"));

		return http.build();
	}

	private MyUsernamePasswordAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) throws Exception {
		MyUsernamePasswordAuthenticationFilter filter = new MyUsernamePasswordAuthenticationFilter(authenticationManager);
		filter.setAuthenticationFailureHandler(authenticationFailureHandler());
		filter.setSecurityContextRepository(new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository()));
		return filter;
	}

	private AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	AuthenticationFailureHandler authenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler();
	}

	@Bean
	static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}
}
