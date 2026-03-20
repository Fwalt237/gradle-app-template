package com.mjc.school.controller.impl;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockBean
    protected ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    protected OAuth2AuthorizedClientService authorizedClientService;

    protected RequestSpecification requestSpecification;

    protected String adminToken;

    @BeforeEach
    void setUp(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "newstags","comments","news", "authors", "tags","user_roles","users");

        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";

        if (adminToken == null) {
            adminToken = obtainAccessToken();
        } else {
            reinsertAdminIntoDb();
        }


        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + adminToken)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    private String obtainAccessToken() {
        String signupRequest = """
                {
                    "username":"admin",
                    "password":"password",
                    "email":"admin@mail.com",
                    "firstName":"Admin",
                    "lastName":"User"
                }
                """;

        given().contentType(ContentType.JSON).body(signupRequest).post("/auth/signup");

        jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES ((SELECT id FROM users WHERE username = 'admin'), 'ROLE_ADMIN')");

        return given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"password\"}")
                .post("/auth/login")
                .then().extract().path("token");
    }

    private void reinsertAdminIntoDb() {
        jdbcTemplate.update("INSERT INTO users (username, password, email, enabled, account_non_expired, credentials_non_expired, account_non_locked) " +
                "VALUES ('admin', 'password', 'admin@mail.com', true, true, true, true)");
        jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES ((SELECT id FROM users WHERE username = 'admin'), 'ROLE_ADMIN')");
    }

    protected String obtainUserToken() {
        String userSignup = """
            {
                "username":"regularUser",
                "password":"password",
                "email":"user@mail.com",
                "firstName":"Regular",
                "lastName":"User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(userSignup)
                .post("/auth/signup");


        jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES ((SELECT id FROM users WHERE username = 'regularUser'), 'ROLE_USER')");

        return given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"regularUser\",\"password\":\"password\"}")
                .post("/auth/login")
                .then().extract().path("token");
    }

}
