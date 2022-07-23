package kr.pwner.fakegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pwner.fakegram.controller.AccountController;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.account.AccountInformationDto;
import kr.pwner.fakegram.dto.account.SignUpDto;
import kr.pwner.fakegram.repository.AccountRepository;
import kr.pwner.fakegram.service.AccountService;
import kr.pwner.fakegram.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.type.NullType;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AccountControllerTest {
    @Autowired
    private AccountController accountController;
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

    private void CreateTemporaryAccount() {
        SignUpDto signUpDto = new SignUpDto()
                .setId("andrew")
                .setPassword("password123")
                .setEmail("test@asd.com")
                .setName("tester");
        accountService.CreateAccount(signUpDto);
    }

    @Transactional
    @Test
    public void CreateAccount() throws Exception {
        SignUpDto signUpDto = new SignUpDto()
                .setId("andrew")
                .setPassword("password123")
                .setEmail("test@asd.com")
                .setName("tester");
        mvc.perform(post("/api/v1/account")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }

    @Transactional
    @Test
    public void GetAccountInformation() throws Exception {
        CreateTemporaryAccount();
        AccountInformationDto accountInformationDto = new AccountInformationDto();
        accountInformationDto
                .setId("andrew")
                .setName("tester")
                .setEmail("test@asd.com");

        mvc.perform(get("/api/v1/account/andrew"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new SuccessResponse<>(accountInformationDto)
                )));
    }

    @Transactional
    @Test
    public void UpdateAccount() throws Exception {
        CreateTemporaryAccount();
        SignUpDto signUpDto = new SignUpDto()
                .setId("andrew123")
                .setPassword("passwordpassword123")
                .setEmail("test_update@asd.com")
                .setName("tester_update");

        mvc.perform(put("/api/v1/account")
                        .header(HttpHeaders.AUTHORIZATION, jwtService.GenerateAccessToken("andrew"))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }

    @Transactional
    @Test
    public void DeleteAccount() throws Exception {
        CreateTemporaryAccount();
        mvc.perform(delete("/api/v1/account")
                        .header(HttpHeaders.AUTHORIZATION, jwtService.GenerateAccessToken("andrew"))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResponse<NullType>())));
    }
}