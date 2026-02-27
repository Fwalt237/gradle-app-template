package com.mjc.school.service.security.oauth2;

import com.mjc.school.service.security.MyUser;
import com.mjc.school.service.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request,response,authentication);

        if(response.isCommitted()){
            logger.debug("Response has already been committed. Unable to redirect to "+targetUrl);
            return;
        }
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        String targetUrl = "http://localhost:8080/api/v1/news?page=0&size=10";
        Object principal = authentication.getPrincipal();

        String username;

        if (principal instanceof MyUser) {
            username = ((MyUser) principal).getUsername();
        }
        else if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;


            String registrationId = ((OAuth2AuthenticationToken) authentication)
                    .getAuthorizedClientRegistrationId();

            OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                    registrationId,
                    oAuth2User.getAttributes()
            );

            username = oauth2UserInfo.getEmail();
            if (username == null || username.isEmpty()) {
                username = oauth2UserInfo.getName();
            }
        } else {
            username = principal.toString();
        }

        String token = jwtUtil.generateToken(username, authentication.getAuthorities());

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }
}
