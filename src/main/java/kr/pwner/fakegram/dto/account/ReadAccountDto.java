package kr.pwner.fakegram.dto.account;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadAccountDto {
    @Data
    @Accessors(chain = true)
    public static class Response {
        private String id;
        private String name;
        private String email;
        private List<Map<String, String>> follower;
        private List<Map<String, String>> following;
    }
}
