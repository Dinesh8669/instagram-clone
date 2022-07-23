package kr.pwner.fakegram.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.dto.ApiResponse.SuccessResponse;
import kr.pwner.fakegram.dto.account.AccountInformationDto;
import kr.pwner.fakegram.dto.account.SignUpDto;
import kr.pwner.fakegram.dto.account.UpdateDto;
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
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

    public ResponseEntity<SuccessResponse<AccountInformationDto>> GetAccountInformation(
            String id
    ) {
        Account account = Optional.ofNullable(accountRepository.findById(id))
                .orElseThrow(()->new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));

        AccountInformationDto accountInformationDto = new AccountInformationDto();
        accountInformationDto
                .setId(account.getId())
                .setName(account.getName())
                .setEmail(account.getEmail());

        return new ResponseEntity<>(new SuccessResponse<>(accountInformationDto), HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<NullType>> CreateAccount(
            final SignUpDto signUpDto
    ) {
        if (!Objects.isNull(accountRepository.findById(signUpDto.getId())))
            throw new ApiException(ExceptionEnum.ACCOUNT_ALREADY_EXISTS);

        Account account = new Account();
        account.setUuid(UUID.randomUUID().toString())
                .setIsActivated(true)
                .setId(signUpDto.getId())
                .setPassword(bCryptPasswordEncoder.encode(signUpDto.getPassword()))
                .setName(signUpDto.getName())
                .setEmail(signUpDto.getEmail())
                .setRole("USER")
                .setCreatedAt(new Date())
                .setUpdatedAt(new Date())
                .setLastSignin(new Date())
                .setRefreshTokenUuid(null);

        accountRepository.save(account);
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<SuccessResponse<NullType>> UpdateAccount(
            String authorization,
            UpdateDto updateDto
    ) {
        DecodedJWT accessToken = jwtService.VerifyJwt(jwtService.getAccessTokenSecret(), authorization);
        if (
                Objects.isNull(updateDto.getId()) &&
                Objects.isNull(updateDto.getPassword()) &&
                Objects.isNull(updateDto.getEmail()) &&
                Objects.isNull(updateDto.getName())
        ) throw new ApiException(ExceptionEnum.NOTHING_INFORMATION_TO_UPDATE);

        String uuid = accessToken.getClaim("uuid").asString();
        Account account = Optional.ofNullable(accountRepository.findByUuidAndIsActivatedTrue(uuid))
                .orElseThrow(()->new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));

        // Insert only if not null
        String updateId = Objects.nonNull(updateDto.getId()) ? updateDto.getId() : account.getId();
        String updatePassword = Objects.nonNull(updateDto.getPassword()) ?
                bCryptPasswordEncoder.encode(updateDto.getPassword()) : account.getPassword();
        String updateEmail = Objects.nonNull(updateDto.getEmail()) ? updateDto.getEmail() : account.getEmail();
        String updateName = Objects.nonNull(updateDto.getName()) ? updateDto.getName() : account.getName();

        account.setId(updateId).setPassword(updatePassword).setEmail(updateEmail).setName(updateName);

        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<SuccessResponse<NullType>> DeleteAccount(
            String authorization
    ) {
        DecodedJWT accessToken = jwtService.VerifyJwt(jwtService.getAccessTokenSecret(), authorization);
        String uuid = accessToken.getClaim("uuid").asString();
        Account account = Optional.ofNullable(accountRepository.findByUuidAndIsActivatedTrue(uuid))
                .orElseThrow(()->new ApiException(ExceptionEnum.ACCOUNT_NOT_EXISTS));
        account.setIsActivated(false).setPassword("");
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }
}