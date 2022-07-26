package kr.pwner.fakegram.dto.account;

import lombok.Data;
import lombok.experimental.Accessors;

public class ReadAccountDto {
    @Data
    @Accessors(chain = true)
    public static class Response {
        private String id;
        private String name;
        private String email;
    }
}
