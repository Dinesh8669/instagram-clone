package kr.pwner.fakegram.controller;

import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.account.AccountInformationDto;
import kr.pwner.fakegram.dto.account.SignUpDto;
import kr.pwner.fakegram.dto.account.UpdateDto;
import kr.pwner.fakegram.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.type.NullType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Validated
@RestController
@RequestMapping(path = "/api/v1/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(
            final AccountService accountService
    ) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<SuccessResponse<AccountInformationDto>> ReadAccount(
            @PathVariable
            @NotBlank
            @Pattern(regexp="^[a-zA-Z0-9]+", message="^[a-zA-Z0-9]+")
            final String id
    ) {
        return accountService.ReadAccount(id);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> CreateAccount(
            @Valid
            @RequestBody
            final SignUpDto signUpDto
    ) {
        return accountService.CreateAccount(signUpDto);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> UpdateAccount(
            @RequestHeader(name = "Authorization")
            final String authorization,
            @RequestBody
            @Valid
            final UpdateDto updateDto
    ) {
        return accountService.UpdateAccount(
                authorization.replace("Bearer ", ""),
                updateDto
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> DeleteAccount(
            @RequestHeader(name = "Authorization")
            final String authorization
    ) {
        return accountService.DeleteAccount(
                authorization.replace("Bearer ", "")
        );
    }
}