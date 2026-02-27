package com.mjc.school.config;

import com.mjc.school.service.security.MyUserDetailsService;
import com.mjc.school.service.security.jwt.JwtAuthenticationFilter;
import com.mjc.school.service.security.oauth2.MyOAuth2UserService;
import com.mjc.school.service.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final MyOAuth2UserService myOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(MyUserDetailsService myUserDetailsService,
                          MyOAuth2UserService myOAuth2UserService,
                          OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter){
        this.myUserDetailsService=myUserDetailsService;
        this.myOAuth2UserService=myOAuth2UserService;
        this.oauth2AuthenticationSuccessHandler=oauth2AuthenticationSuccessHandler;
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider  authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;

    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.
                cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\": \"Unauthorized access - please login\"}");
                    })
                .and()
                .authorizeRequests()
                    .antMatchers("/api/v*/auth/**").permitAll()
                    .antMatchers("/oauth2/**").permitAll()
                    .antMatchers("/swagger-ui/**","/swagger-resources/**","/v*/api-docs").permitAll()

                    .antMatchers(HttpMethod.GET,"/api/v*/news/**").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/v*/authors/**").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/v*/tags/**").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/v*/comments/**").permitAll()

                    .antMatchers(HttpMethod.POST,"/api/v*/news/**").hasAnyRole("USER","ADMIN")
                    .antMatchers(HttpMethod.POST,"/api/v*/comments/**").hasAnyRole("USER","ADMIN")

                    .antMatchers(HttpMethod.POST,"/api/v*/authors/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/api/v*/tags/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PATCH,"/api/v*/*/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT,"/api/v*/*/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE,"/api/v*/*/**").hasRole("ADMIN")

                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                    .userInfoEndpoint()
                        .userService(myOAuth2UserService)
                    .and()
                    .successHandler(oauth2AuthenticationSuccessHandler)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
