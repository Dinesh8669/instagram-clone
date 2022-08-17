package kr.pwner.fakegram.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.model.Account;
import kr.pwner.fakegram.model.Feed;
import kr.pwner.fakegram.repository.AccountRepository;
import kr.pwner.fakegram.repository.FeedRepository;
import org.springframework.stereotype.Service;

// ToDo: Implement feed pagination and upload feed(with images)
@Service
public class FeedService {
    private JwtService jwtService;
    private AccountRepository accountRepository;
    private FeedRepository feedRepository;

    public FeedService(
            JwtService jwtService,
            AccountRepository accountRepository,
            FeedRepository feedRepository
    ) {
        this.jwtService = jwtService;
        this.accountRepository = accountRepository;
        this.feedRepository = feedRepository;
    }

    public void CreateFeed(String authorization) {
        DecodedJWT decodedJWT = jwtService.VerifyAccessToken(
                authorization.replace("Bearer ", "")
        );
        Long accountIdx = decodedJWT.getClaim("idx").asLong();
        Account account = accountRepository.findByIdxAndIsActivateTrue(accountIdx);
        Feed feed = Feed.builder().content("testing").build();
        feed.LinkAccount(account);
        feedRepository.save(feed);
    }
}
