package kr.pwner.fakegram.service;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.TokenDto;
import kr.pwner.fakegram.dto.auth.SignInDto;
import kr.pwner.fakegram.dto.auth.SignInResponseDto;
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
        Account account = Optional.ofNullable(accountRepository.findByIdAndIsActivatedTrue(id))
                .orElseThrow(()->new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));
        if (!bCryptPasswordEncoder.matches(password, account.getPassword()))
            throw new ApiException(ExceptionEnum.INCORRECT_ACCOUNT_PASSWORD);
        return account;
    }

    public ResponseEntity<SuccessResponse<SignInResponseDto>> SignIn(final SignInDto accountDto) {
        Account account = ValidateAccount(accountDto.getId(), accountDto.getPassword());
        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto
                .setAccessTokenExpiresIn(String.valueOf(jwtService.getAccessTokenExpiresIn() / 1000))
                .setRefreshTokenExpiresIn(String.valueOf(jwtService.getRefreshTokenExpiresIn() / 1000))
                .setAccessToken(jwtService.GenerateAccessToken(account.getId()))
                .setRefreshToken(jwtService.GenerateRefreshToken(account.getId()));
        return new ResponseEntity<>(new SuccessResponse<>(signInResponseDto), HttpStatus.OK);
    }

    //NullPointerException: Invalid token
    public ResponseEntity<SuccessResponse<String>> Refresh(final TokenDto tokenDto) {
        DecodedJWT refreshToken;
        try {
            refreshToken = jwtService.VerifyJwt(jwtService.getRefreshTokenSecret(), tokenDto.getRefreshToken());
        } catch (NullPointerException | JWTDecodeException e) {
            throw new ApiException(ExceptionEnum.INVALID_OR_EXPIRED_TOKEN);
        }
        String uuid = refreshToken.getClaim("uuid").asString();
        Account account = Optional.ofNullable(accountRepository.findByUuid(uuid))
                .orElseThrow(()->new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));

        String dbRefreshTokenUuid = account.getRefreshTokenUuid();
        String RequestRefreshTokenUuid = refreshToken.getClaim("refreshTokenUuid").asString();

        if (!Objects.equals(dbRefreshTokenUuid, RequestRefreshTokenUuid))
            throw new ApiException(ExceptionEnum.INVALID_OR_EXPIRED_TOKEN);

        String accessToken = jwtService.GenerateAccessToken(account.getId());
        return new ResponseEntity<>(new SuccessResponse<>(accessToken), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<SuccessResponse<NullType>> SignOut(DecodedJWT accessToken) {
        String uuid = accessToken.getClaim("uuid").asString();
        Account account = accountRepository.findByUuid(uuid);
        Optional.ofNullable(account.getRefreshTokenUuid())
                .orElseThrow(()->new ApiException(ExceptionEnum.ALREADY_SIGN_OUT));
        account.setRefreshTokenUuid(null);
        accountRepository.save(account);
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }
}