package kr.pwner.fakegram.service;

import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.follow.FollowDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.lang.model.type.NullType;

@Service
public class FollowService {
    public ResponseEntity<SuccessResponse<NullType>> Follow(
            FollowDto.Request request
    ) {

        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }
}
