package com.calendar.controller;

import com.calendar.dto.auth.LoginReq;
import com.calendar.dto.auth.LogoutReq;
import com.calendar.dto.auth.TokenReq;
import com.calendar.entity.User;
import com.calendar.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("tester")
                .build();
        userRepository.save(user);
    }

    @Test
    void 로그인_성공() throws Exception {
        LoginReq req = new LoginReq();
        req.setEmail("test@test.com");
        req.setPassword("1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void 로그인_실패_잘못된비밀번호() throws Exception {
        LoginReq req = new LoginReq();
        req.setEmail("test@test.com");
        req.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void 재발급_실패_없는토큰() throws Exception {
        TokenReq req = new TokenReq();
        req.setEmail("test@test.com");
        req.setRefreshToken("invalid-token");

        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void 로그아웃_성공() throws Exception {
        LogoutReq req = new LogoutReq();
        req.setEmail("test@test.com");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }
}
