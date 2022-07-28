package kr.pwner.fakegram.service;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.auth.RefreshDto;
import kr.pwner.fakegram.dto.auth.SignInDto;
import kr.pwner.fakegram.exception.ApiException;
import kr.pwner.fakegram.exception.ExceptionEnum;
import kr.pwner.fakegram.model.Account;
import kr.pwner.fakegram.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.type.NullType;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public AuthService(
            final AccountRepository accountRepository,
            final BCryptPasswordEncoder bCryptPasswordEncoder,
            final JwtService jwtService
    ) {
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    private Account ValidateAccount(final String id, final String password) {
        Account account = Optional.ofNullable(accountRepository.findByIdAndIsActivateTrue(id))
                .orElseThrow(() -> new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));
        if (!bCryptPasswordEncoder.matches(password, account.getPassword()))
            throw new ApiException(ExceptionEnum.INCORRECT_ACCOUNT_PASSWORD);
        return account;
    }

    public ResponseEntity<SuccessResponse<SignInDto.Response>> SignIn(
            final SignInDto.Request request
    ) {
        Account account = ValidateAccount(request.getId(), request.getPassword());
        SignInDto.Response response = new SignInDto.Response();
        response.setAccessTokenExpiresIn(String.valueOf(jwtService.getAccessTokenExpiresIn() / 1000))
                .setRefreshTokenExpiresIn(String.valueOf(jwtService.getRefreshTokenExpiresIn() / 1000))
                .setAccessToken(jwtService.GenerateAccessToken(account.getId()))
                .setRefreshToken(jwtService.GenerateRefreshToken(account.getId()));
        return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
    }

    // need refactor
    public ResponseEntity<SuccessResponse<RefreshDto.Response>> Refresh(
            final RefreshDto.Request request
    ) {
        DecodedJWT refreshToken;
        try {
            //  It's not validated on the interceptor because refreshToken is passed via body
            refreshToken = jwtService.VerifyJwt(
                    jwtService.getRefreshTokenSecret(),
                    request.getRefreshToken()
            );
        } catch (NullPointerException | JWTDecodeException e) {
            throw new ApiException(ExceptionEnum.INVALID_OR_EXPIRED_TOKEN);
        }
        Long idx = refreshToken.getClaim("idx").asLong();
        Account account = Optional.ofNullable(accountRepository.findByIdxAndIsActivateTrue(idx))
                .orElseThrow(() -> new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));

        String dbRefreshToken = account.getRefreshToken();
        String RequestRefreshToken = refreshToken.getClaim("refreshToken").asString();

        if (!Objects.equals(dbRefreshToken, RequestRefreshToken))
            throw new ApiException(ExceptionEnum.INVALID_OR_EXPIRED_TOKEN);

        RefreshDto.Response response = new RefreshDto.Response().setAccessToken(
                jwtService.GenerateAccessToken(account.getId())
        );
        return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<SuccessResponse<NullType>> SignOut(
            String authorization
    ) {
        DecodedJWT accessToken = jwtService.VerifyJwt(
                jwtService.getAccessTokenSecret(),
                authorization.replace("Bearer ", "")
        );
        Long idx = accessToken.getClaim("idx").asLong();
        Account account = accountRepository.findByIdxAndIsActivateTrue(idx);
        Optional.ofNullable(account.getRefreshToken())
                .orElseThrow(() -> new ApiException(ExceptionEnum.ALREADY_SIGN_OUT));
        account.SignOut();
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }
}