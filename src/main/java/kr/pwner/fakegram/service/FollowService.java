package kr.pwner.fakegram.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.follow.FollowDto;
import kr.pwner.fakegram.exception.ApiException;
import kr.pwner.fakegram.exception.ExceptionEnum;
import kr.pwner.fakegram.model.Follow;
import kr.pwner.fakegram.repository.AccountRepository;
import kr.pwner.fakegram.repository.FollowRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.type.NullType;
import java.util.Objects;
import java.util.Optional;

@Service
public class FollowService {
    FollowRepository followRepository;
    AccountRepository accountRepository;
    JwtService jwtService;

    public FollowService(
            FollowRepository followRepository,
            AccountRepository accountRepository,
            JwtService jwtService
    ) {
        this.followRepository = followRepository;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public ResponseEntity<SuccessResponse<NullType>> Follow(
            String authorization,
            FollowDto.Request request
    ) {
        DecodedJWT accessToken = jwtService.VerifyJwt(
                jwtService.getAccessTokenSecret(),
                authorization.replace("Bearer ", "")
        );
        // The access token will be verified on the interceptor
        Long sourceIdx = accessToken.getClaim("idx").asLong();
        Long targetIdx = Optional.ofNullable(accountRepository.findByIdAndIsActivateTrue(request.getTargetId()))
                .orElseThrow(() -> new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS)).getIdx();

        Follow followHistory = followRepository.findBySourceIdxAndTargetIdx(sourceIdx, targetIdx);

        if (Objects.isNull(followHistory)) { // do follow
            Follow follow = Follow.builder()
                    .sourceIdx(sourceIdx)
                    .targetIdx(targetIdx)
                    .build();
            followRepository.save(follow);
        } else { // unfollow
            followRepository.deleteByIdx(followHistory.getIdx());
        }
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }
}
