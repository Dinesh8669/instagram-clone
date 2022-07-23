package kr.pwner.fakegram.dto.account;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class AccountInformationDto {
    private String id;
    private String name;
    private String email;
}
