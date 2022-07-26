package kr.pwner.fakegram.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.account.CreateAccountDto;
import kr.pwner.fakegram.dto.account.ReadAccountDto;
import kr.pwner.fakegram.dto.account.UpdateAccountDto;
import kr.pwner.fakegram.exception.ApiException;
import kr.pwner.fakegram.exception.ExceptionEnum;
import kr.pwner.fakegram.model.Account;
import kr.pwner.fakegram.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.type.NullType;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public AccountService(
            AccountRepository accountRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JwtService jwtService
    ) {
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    public ResponseEntity<SuccessResponse<NullType>> CreateAccount(
            final CreateAccountDto.Request signUpDto
    ) {
        if (Objects.nonNull(accountRepository.findById(signUpDto.getId())))
            throw new ApiException(ExceptionEnum.ACCOUNT_ALREADY_EXISTS);

        Account account = Account.builder()
                .id(signUpDto.getId())
                .password(bCryptPasswordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .email(signUpDto.getEmail())
                .build();

        accountRepository.save(account);
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<ReadAccountDto.Response>> ReadAccount(
            final String id
    ) {
        Account account = Optional.ofNullable(accountRepository.findById(id))
                .orElseThrow(() -> new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));

        ReadAccountDto.Response response = new ReadAccountDto.Response()
                .setId(account.getId())
                .setName(account.getName())
                .setEmail(account.getEmail());

        return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<SuccessResponse<NullType>> UpdateAccount(
            final String authorization,
            final UpdateAccountDto.Request request
    ) {
        DecodedJWT accessToken = jwtService.VerifyJwt(
                jwtService.getAccessTokenSecret(),
                authorization.replace("Bearer ", "")
        );

        if (Objects.isNull(request.getId()) &&
                Objects.isNull(request.getPassword()) &&
                Objects.isNull(request.getEmail()) &&
                Objects.isNull(request.getName())
        ) throw new ApiException(ExceptionEnum.NOTHING_INFORMATION_TO_UPDATE);

        String uuid = accessToken.getClaim("uuid").asString();
        Account account = Optional.ofNullable(accountRepository.findByUuidAndIsActivatedTrue(uuid))
                .orElseThrow(() -> new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));

        String encryptedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        // Insert only if not null
        String updateId = Objects.nonNull(request.getId()) ? request.getId() : account.getId();
        String updatePassword = Objects.nonNull(request.getPassword()) ? encryptedPassword : account.getPassword();
        String updateEmail = Objects.nonNull(request.getEmail()) ? request.getEmail() : account.getEmail();
        String updateName = Objects.nonNull(request.getName()) ? request.getName() : account.getName();

        account.Update(updateId, updatePassword, updateName, updateEmail);
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<SuccessResponse<NullType>> DeleteAccount(
            final String authorization
    ) {
        DecodedJWT accessToken = jwtService.VerifyJwt(
                jwtService.getAccessTokenSecret(),
                authorization.replace("Bearer ", "")
        );

        String uuid = accessToken.getClaim("uuid").asString();
        Account account = Optional.ofNullable(accountRepository.findByUuidAndIsActivatedTrue(uuid))
                .orElseThrow(() -> new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));
        account.Delete();
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }
}