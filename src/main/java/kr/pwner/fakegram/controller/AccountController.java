package kr.pwner.fakegram.controller;

import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.account.CreateAccountDto;
import kr.pwner.fakegram.dto.account.ReadAccountDto;
import kr.pwner.fakegram.dto.account.UpdateAccountDto;
import kr.pwner.fakegram.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.type.NullType;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> CreateAccount(
            @Valid @RequestBody final CreateAccountDto.Request request
    ) {
        return accountService.CreateAccount(request);
    }

    @RequestMapping(value = "/{id:^[a-zA-Z0-9]+}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<SuccessResponse<ReadAccountDto.Response>> ReadAccount(
            @Valid @PathVariable final String id
    ) {
        return accountService.ReadAccount(id);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> UpdateAccount(
            @RequestHeader(name = "Authorization") final String authorization,
            @Valid @RequestBody final UpdateAccountDto.Request request
    ) {
        return accountService.UpdateAccount(authorization, request);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> DeleteAccount(
            @RequestHeader(name = "Authorization") final String authorization
    ) {
        return accountService.DeleteAccount(authorization);
    }

    @RequestMapping(value = "/upload/profileImage", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<SuccessResponse<String>> UploadProfileImage(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestParam("file") MultipartFile file
    ) {
        return accountService.UploadProfileImage(authorization, file);
    }
}