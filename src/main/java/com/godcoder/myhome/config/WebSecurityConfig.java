package com.godcoder.myhome.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private DataSource dataSource;      //application.properties를 사용할수 있도록 선언

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/account/register", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/account/login")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication()                   //데이터 소스를 넘겨주면 스프링에서 자동으로 인증처리
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder()) // 인증처리할 때 비밀번호 암호화를 자동으로 처리
                .usersByUsernameQuery("select username, password, enabled "      //인증
                        + "from user "
                        + "where username = ?")
                .authoritiesByUsernameQuery("select u.username, r.name "       //권한
                        + "from user_role ur inner join user u on ur.user_id = u.id "
                        + "inner join role r on ur.role_id = r.id "
                        + "where u.username = ?");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();     //스프링에서 지원하는 인코딩 방식
    }
}