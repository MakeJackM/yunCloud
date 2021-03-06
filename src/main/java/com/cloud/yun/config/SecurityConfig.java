package com.cloud.yun.config;

import com.cloud.yun.constants.URLConstants;
import com.cloud.yun.jwt.JwtAuthenticationEntryPoint;
import com.cloud.yun.jwt.PasswordEncoder;
import com.cloud.yun.jwt.handler.JwtAccessDeniedHandler;
import com.cloud.yun.jwt.handler.JwtLoginFailureHandler;
import com.cloud.yun.jwt.handler.JwtLoginSuccessHandler;
import com.cloud.yun.jwt.handler.JwtLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @ClassName SecurityConfig
 * @Description SecurityConfig is to handle xxxx
 * @Author jack
 * @Date 7/12/2022 5:02 PM
 * @Version 1.0
 **/
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

	@Autowired
	private JwtLoginSuccessHandler jwtLoginSuccessHandler;

	@Autowired
	private JwtLoginFailureHandler jwtLoginFailureHandler;

	@Autowired
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	private static final String[] URL_WHITELIST = {
			"/login",
			"/logout",
			"/captcha",
			"/favicon.ico",
			"/user/save"
	};

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	PasswordEncoder PasswordEncoder() {
		return new PasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//?????????????????? user/login   anonymous()??????????????????????????????
		http.authorizeRequests()
				.antMatchers(URLConstants.login).anonymous()
				//permitAll() ??????????????????????????????????????????
				.antMatchers("/index")
				.permitAll()
				//????????????????????????????????????
				.and().authorizeRequests();

		http.cors().and().csrf().disable()
				.formLogin()
				.loginPage(URLConstants.login)
				.successHandler(jwtLoginSuccessHandler)
				.failureHandler(jwtLoginFailureHandler)

				.and()
				.logout()
				.logoutSuccessHandler(jwtLogoutSuccessHandler)

				// ??????session
                .and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// ??????????????????
				.and()
				.authorizeRequests()
				.antMatchers(URL_WHITELIST).permitAll()
				.anyRequest().authenticated()
				// ???????????????
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)
				// ???????????????????????????
				//.and()
				//.addFilter(jwtAuthenticationFilter())
				// ????????????????????????UsernamePassword???????????????
				//.addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
		;

	}
}
