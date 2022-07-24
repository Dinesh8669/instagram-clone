package kr.pwner.fakegram.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.TokenDto;
import kr.pwner.fakegram.dto.auth.SignInDto;
import kr.pwner.fakegram.dto.auth.SignInResponseDto;
import kr.pwner.fakegram.service.AuthService;
import kr.pwner.fakegram.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.type.NullType;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            final AuthService authService,
            final JwtService jwtService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
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
