package kr.pwner.fakegram.controller;

import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.follow.FollowDto;
import kr.pwner.fakegram.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.type.NullType;

@RequestMapping(path = "/api/v1/follow")
@RestController
public class FollowController {
    FollowService followService;
    public FollowController(FollowService followService){
        this.followService = followService;
    }
    // Follow someone with own token uuid
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<SuccessResponse<NullType>> Follow(
            @RequestHeader(name = "Authorization") final String authorization,
            @RequestBody final FollowDto.Request request
    ) {
        // if follow data isa exists, unfollow it
        return followService.Follow(request);
    }
}
