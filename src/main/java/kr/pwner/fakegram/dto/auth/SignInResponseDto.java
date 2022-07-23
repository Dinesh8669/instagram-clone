package kr.pwner.fakegram.dto.auth;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class SignInResponseDto {
    private String accessTokenExpiresIn;
    private String refreshTokenExpiresIn;
    private String accessToken;
    private String refreshToken;
}
