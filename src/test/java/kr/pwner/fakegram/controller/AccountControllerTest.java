package kr.pwner.fakegram.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.account.CreateAccountDto;
import kr.pwner.fakegram.dto.account.ReadAccountDto;
import kr.pwner.fakegram.dto.account.UpdateAccountDto;
import kr.pwner.fakegram.dto.follow.FollowDto;
import kr.pwner.fakegram.repository.AccountRepository;
import kr.pwner.fakegram.repository.FollowRepository;
import kr.pwner.fakegram.service.AccountService;
import kr.pwner.fakegram.service.FollowService;
import kr.pwner.fakegram.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.type.NullType;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AccountControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private FollowService followService;
    @Autowired
    private FollowRepository followRepository;

    private final String TESTER_ID = "TeSteR";
    private final String OTHER_ID = "other";
    private final String TESTER_PW = "password123";
    private final String TESTER_EMAIL = "testtest@test.com";
    private final String TESTER_NAME = "tester!";


    private void CreateTemporaryAccount(String id) {
        CreateAccountDto.Request request = new CreateAccountDto.Request()
                .setId(id)
                .setPassword(TESTER_PW)
                .setEmail(TESTER_EMAIL)
                .setName(TESTER_NAME);
        accountService.CreateAccount(request);
    }

    @Transactional
    @Test
    public void CreateAccount() throws Exception {
        CreateAccountDto.Request request = new CreateAccountDto.Request()
                .setId(TESTER_ID)
                .setPassword(TESTER_PW)
                .setEmail(TESTER_EMAIL)
                .setName(TESTER_NAME);
        mvc.perform(post("/api/v1/account")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }

    // ToDo: Refactor auth and follow test code
    @Transactional
    @Test
    public void ReadAccount() throws Exception {
        CreateTemporaryAccount(TESTER_ID);
        CreateTemporaryAccount(OTHER_ID);

        FollowDto.Request followDto = new FollowDto.Request().setTargetId(TESTER_ID);
        followService.Follow(jwtService.GenerateAccessToken(OTHER_ID), followDto);


        List<Map<String, String>> follower = followRepository.getFollowerByIdx(
                accountRepository.findById(TESTER_ID).getIdx()
        );

        List<Map<String, String>> following = followRepository.getFollowingByIdx(
                accountRepository.findById(TESTER_ID).getIdx()
        );

        ReadAccountDto.Response response = new ReadAccountDto.Response()
                .setId(TESTER_ID)
                .setName(TESTER_NAME)
                .setEmail(TESTER_EMAIL)
                .setFollower(follower)
                .setFollowing(following);

        mvc.perform(get("/api/v1/account/" + TESTER_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new SuccessResponse<>(response)
                )));
    }

    @Transactional
    @Test
    public void UpdateAccount() throws Exception {
        CreateTemporaryAccount(TESTER_ID);
        UpdateAccountDto.Request request = new UpdateAccountDto.Request()
                .setId(TESTER_ID + "123")
                .setPassword(TESTER_PW + "123")
                .setEmail("asd" + TESTER_EMAIL)
                .setName(TESTER_NAME + "123");

        mvc.perform(put("/api/v1/account")
                        .header(HttpHeaders.AUTHORIZATION, jwtService.GenerateAccessToken(TESTER_ID))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }

    @Transactional
    @Test
    public void DeleteAccount() throws Exception {
        CreateTemporaryAccount(TESTER_ID);
        mvc.perform(delete("/api/v1/account")
                        .header(HttpHeaders.AUTHORIZATION, jwtService.GenerateAccessToken(TESTER_ID))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }
}