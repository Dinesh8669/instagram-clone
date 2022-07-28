package kr.pwner.fakegram.dto.follow;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class FollowDto {
    @Data
    @Accessors(chain = true)
    public static class Request{
        @NotBlank(message = "targetId field is mandatory")
        @Size(min = 4, max = 20, message = "id field must be between 4 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]+", message = "^[a-zA-Z0-9]+")
        private String targetId;
    }
}
