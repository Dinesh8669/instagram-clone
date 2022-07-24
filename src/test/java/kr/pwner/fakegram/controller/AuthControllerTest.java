package kr.pwner.fakegram.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pwner.fakegram.Application;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.TokenDto;
import kr.pwner.fakegram.dto.account.SignUpDto;
import kr.pwner.fakegram.dto.auth.SignInDto;
import kr.pwner.fakegram.dto.auth.SignInResponseDto;
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

    public void CreateTemporaryAccount() {
        SignUpDto signUpDto = new SignUpDto()
                .setId("andrew")
                .setPassword("password123")
                .setEmail("test@asd.com")
                .setName("tester");
        accountService.CreateAccount(signUpDto);
    }

    @Transactional
    @Test
    public void SignIn() throws Exception {
        CreateTemporaryAccount();
        SignInDto signInDto = new SignInDto();
        signInDto.setId("andrew").setPassword("password123");

        String response = mvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // https://stackoverflow.com/questions/11664894/jackson-deserialize-using-generic-class
        SuccessResponse<SignInResponseDto> successResponse = objectMapper.readValue(
                response,
                new TypeReference<>() {}
        );
        String accessToken = successResponse.getData().getAccessToken();
        DecodedJWT decodedJWT = jwtService.VerifyJwt(jwtService.getAccessTokenSecret(), accessToken);

        decodedJWT.getClaim("uuid");
        assertEquals(
                decodedJWT.getClaim("uuid").asString(),
                accountRepository.findById("andrew").getUuid());
    }

    @Transactional
    @Test
    public void Refresh() throws Exception {
        CreateTemporaryAccount();
        String refreshToken = jwtService.GenerateRefreshToken("andrew");

        TokenDto tokenDto = new TokenDto();
        tokenDto.setRefreshToken(refreshToken);

        String response = mvc.perform(put("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        SuccessResponse<String> successResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        DecodedJWT accessToken = jwtService.VerifyJwt(jwtService.getAccessTokenSecret(), successResponse.getData());

        String uuid = accessToken.getClaim("uuid").asString();
        Account account = accountRepository.findByUuid(accessToken.getClaim("uuid").asString());

        assertEquals(uuid, account.getUuid());
    }

    @Transactional
    @Test
    public void SignOut() throws Exception {
        CreateTemporaryAccount();
        String accessToken = jwtService.GenerateAccessToken("andrew"); //for sign out
        jwtService.GenerateRefreshToken("andrew"); // for generate refresh token uuid
        mvc.perform(delete("/api/v1/auth")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }
}
