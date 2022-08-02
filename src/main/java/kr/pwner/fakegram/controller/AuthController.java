package kr.pwner.fakegram.controller;

import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.auth.RefreshDto;
import kr.pwner.fakegram.dto.auth.SignInDto;
import kr.pwner.fakegram.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.type.NullType;
import javax.validation.Valid;

@RequestMapping(path = "/api/v1/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(
            final AuthService authService
    ) {
        this.authService = authService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<SuccessResponse<SignInDto.Response>> SignIn(
            @Valid @RequestBody final SignInDto.Request request
    ) {
        return authService.SignIn(request);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<SuccessResponse<RefreshDto.Response>> Refresh(
            @Valid @RequestBody final RefreshDto.Request request
    ) {
        return authService.Refresh(request);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> SignOut(
            @RequestHeader(name = "Authorization") final String authorization
    ) {
        return authService.SignOut(authorization);
    }
}
