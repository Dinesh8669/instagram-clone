package kr.pwner.fakegram.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class TokenDto {
    @NotBlank(message="refreshToken field is mandatory")
    @Pattern(regexp="(^[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$)",
            message = "Invalid Token Format")
    String refreshToken;
}