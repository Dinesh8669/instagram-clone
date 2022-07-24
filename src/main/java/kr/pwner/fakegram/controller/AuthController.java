package kr.pwner.fakegram.controller;

import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.TokenDto;
import kr.pwner.fakegram.dto.auth.SignInDto;
import kr.pwner.fakegram.dto.auth.SignInResponseDto;
import kr.pwner.fakegram.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.type.NullType;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(
            final AuthService authService
    ) {
        this.authService = authService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<SuccessResponse<SignInResponseDto>> SignIn(
            @Valid
            @RequestBody
            final SignInDto signInDto
    ) {
        return authService.SignIn(signInDto);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<SuccessResponse<String>> Refresh(
            @Valid
            @RequestBody
            final TokenDto tokenDto
    ) {
        return authService.Refresh(tokenDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> SignOut(
            @RequestHeader(name = "Authorization")
            String authorization
    ) {
        return authService.SignOut(
                authorization.replace("Bearer ", "")
        );
    }
}
