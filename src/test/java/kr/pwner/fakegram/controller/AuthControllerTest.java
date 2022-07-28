package kr.pwner.fakegram.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pwner.fakegram.Application;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.account.CreateAccountDto;
import kr.pwner.fakegram.dto.auth.RefreshDto;
import kr.pwner.fakegram.dto.auth.SignInDto;
import kr.pwner.fakegram.model.Account;
import kr.pwner.fakegram.repository.AccountRepository;
import kr.pwner.fakegram.service.AccountService;
import kr.pwner.fakegram.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.type.NullType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AccountRepository accountRepository;

    private final String TESTER_ID = "TeSteR";
    private final String TESTER_PW = "password123";
    private final String TESTER_EMAIL = "testtest@test.com";
    private final String TESTER_NAME = "tester!";


    private void CreateTemporaryAccount() {
        CreateAccountDto.Request request = new CreateAccountDto.Request()
                .setId(TESTER_ID)
                .setPassword(TESTER_PW)
                .setEmail(TESTER_EMAIL)
                .setName(TESTER_NAME);
        accountService.CreateAccount(request);
    }

    @Transactional
    @Test
    public void SignIn() throws Exception {
        CreateTemporaryAccount();
        SignInDto.Request request = new SignInDto.Request()
                .setId(TESTER_ID)
                .setPassword(TESTER_PW);

        String response = mvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // https://stackoverflow.com/questions/11664894/jackson-deserialize-using-generic-class
        SuccessResponse<SignInDto.Response> successResponse = objectMapper.readValue(
                response,
                new TypeReference<>() {}
        );

        DecodedJWT decodedJWT = jwtService.VerifyJwt(
                jwtService.getAccessTokenSecret(),
                successResponse.getData().getAccessToken()
        );
        assertEquals(
                decodedJWT.getClaim("idx").asLong(),
                accountRepository.findById(TESTER_ID).getIdx()
        );
    }

    @Transactional
    @Test
    public void Refresh() throws Exception {
        CreateTemporaryAccount();
        String refreshToken = jwtService.GenerateRefreshToken(TESTER_ID);
        RefreshDto.Request request = new RefreshDto.Request().setRefreshToken(refreshToken);

        String response = mvc.perform(put("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        SuccessResponse<RefreshDto.Response> successResponse = objectMapper.readValue(
                response,
                new TypeReference<>() {}
        );
        DecodedJWT accessToken = jwtService.VerifyJwt(
                jwtService.getAccessTokenSecret(),
                successResponse.getData().getAccessToken()
        );

        Long idx = accessToken.getClaim("idx").asLong();
        Account account = accountRepository.findByIdxAndIsActivateTrue(idx);

        assertEquals(
                idx,
                account.getIdx()
        );
    }

    @Transactional
    @Test
    public void SignOut() throws Exception {
        CreateTemporaryAccount();
        String accessToken = jwtService.GenerateAccessToken(TESTER_ID); //for sign out
        jwtService.GenerateRefreshToken(TESTER_ID); // for generate refresh token uuid
        mvc.perform(delete("/api/v1/auth")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }
}
