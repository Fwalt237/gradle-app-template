package com.mjc.school.controller.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("OAuth2 with Google - Should trigger SuccessHandler and return JWT")
    void oauth2GoogleLogin_ShouldRedirectToSuccessHandler() throws Exception {
        mockMvc.perform(get("/login/oauth2/code/google")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("sub", "123456");
                                    attrs.put("name", "Rod Johnson");
                                    attrs.put("email", "rod@gmail.com");
                                })
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                        ))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("OAuth2 with GitHub - Should trigger SuccessHandler and return JWT")
    void oauth2GithubLogin_ShouldRedirectToSuccessHandler() throws Exception {
        mockMvc.perform(get("/login/oauth2/code/github")
                        .with(oauth2Login()
                                .attributes(attrs -> {
                                    attrs.put("id", "123456");
                                    attrs.put("login", "Rod Johnson");
                                    attrs.put("email", "rod@github.com");
                                })
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                        ))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists("Location"));
    }
}
