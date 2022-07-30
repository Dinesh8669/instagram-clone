package kr.pwner.fakegram.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.model.Account;
import kr.pwner.fakegram.repository.AccountRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {
    private final AccountRepository accountRepository;

    public JwtService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Getter @Setter
    @Value("${env.JWT_ACCESS_SECRET}")
    private String accessTokenSecret;

    @Getter @Setter
    @Value("${env.JWT_REFRESH_SECRET}")
    private String refreshTokenSecret;

    private final long accessTokenExpiresIn = 1000 * 60 * 15; // 15 minute
    private final long refreshTokenExpiresIn = 1000 * 60 * 60 * 24; // 1 day

    public long getAccessTokenExpiresIn(){
        return this.accessTokenExpiresIn + new Date().getTime();
    }

    public long getRefreshTokenExpiresIn(){
        return this.refreshTokenExpiresIn + new Date().getTime();
    }

    public DecodedJWT VerifyJwt(final String secret, final String token) {
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    //need to generate random access token
    public String GenerateAccessToken(final String id) {
        Account account = accountRepository.findByIdAndIsActivateTrue(id);
        return  JWT.create()
                .withExpiresAt(new Date(getAccessTokenExpiresIn()))
                .withClaim("idx", account.getIdx())
                .sign(Algorithm.HMAC256(accessTokenSecret));
    }

    @Transactional
    public String GenerateRefreshToken(final String id) {
        Account account = accountRepository.findByIdAndIsActivateTrue(id);
        account.SignIn(UUID.randomUUID().toString());
        return JWT.create()
                .withExpiresAt(new Date(getRefreshTokenExpiresIn()))
                .withClaim("idx", account.getIdx())
                .withClaim("refreshToken", account.getRefreshToken())
                .sign(Algorithm.HMAC256(refreshTokenSecret));
    }
}