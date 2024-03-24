package com.zubayer.entity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
		"/login-assets/**",
		"/assets/**",
		"/register/**",
		"/business/**",
		"/clearlogincache",
	};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(ALLOWED_URLS).permitAll()
				.anyRequest().authenticated()
			)
			.userDetailsService(userService)
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





	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
