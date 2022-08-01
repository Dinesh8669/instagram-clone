package kr.pwner.fakegram.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import kr.pwner.fakegram.exception.ApiException;
import kr.pwner.fakegram.exception.ExceptionEnum;
import kr.pwner.fakegram.model.Upload;
import kr.pwner.fakegram.repository.UploadRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UploadService {
    private final Path uploadLocation;
    private final JwtService jwtService;
    private final UploadRepository uploadRepository;

    public UploadService(JwtService jwtService, UploadRepository uploadRepository) throws IOException {
        this.jwtService = jwtService;
        this.uploadRepository = uploadRepository;

        this.uploadLocation = Paths.get("./uploads").toAbsolutePath().normalize();
        Files.createDirectories(this.uploadLocation);
    }

    public String UploadFile(
            String authorization,
            MultipartFile file
    ) {
        DecodedJWT accessToken = jwtService.VerifyJwt(
                jwtService.getAccessTokenSecret(),
                authorization.replace("Bearer ", "")
        );
        // Sanity check
        if (file.isEmpty())
            throw new ApiException(ExceptionEnum.EMPTY_FILE);
        // if deploy on production, you have to make more filter
        if (StringUtils.cleanPath(file.getOriginalFilename()).contains(".."))
            throw new ApiException(ExceptionEnum.INVALID_FILE_NAME);

        //  for security, don't use user's  filename
        String fileUuid = UUID.randomUUID().toString();
        Path fileLocation = this.uploadLocation.resolve(fileUuid);

        try {
            Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ApiException(ExceptionEnum.COULD_NOT_SAVE_THE_FILE);
        }
        // save file attribute to db

        Upload upload = Upload.builder()
                .accountIdx(accessToken.getClaim("idx").asLong())
                .fileUuid(fileUuid)
                .build();

        uploadRepository.save(upload);

        return fileUuid;
    }

}
