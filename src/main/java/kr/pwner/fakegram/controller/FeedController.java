package kr.pwner.fakegram.controller;

import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.type.NullType;

@RequestMapping(path = "/api/v1/feed")
@RestController
public class FeedController {
    FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<SuccessResponse<NullType>> CreateFeed(
            @RequestHeader(name = "Authorization") final String authorization
//            @RequestParam(name="files") MultipartFile[] files
    ) {
        feedService.CreateFeed();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
