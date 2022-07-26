package kr.pwner.fakegram.dto.account;

import lombok.Data;
import lombok.experimental.Accessors;

public class ReadAccountDto {
//    @Getter
//    public static class Request{
//        @NotBlank
//        @Pattern(regexp="^[a-zA-Z0-9]+", message="^[a-zA-Z0-9]+")
//        private String id;
//    }
    @Data
    @Accessors(chain = true)
    public static class Response {
        private String id;
        private String name;
        private String email;
    }
}
