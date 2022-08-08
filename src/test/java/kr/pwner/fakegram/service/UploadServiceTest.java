package kr.pwner.fakegram.service;

import kr.pwner.fakegram.exception.ApiException;
import kr.pwner.fakegram.exception.ExceptionEnum;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@SpringBootTest
public class UploadServiceTest {
    private final Path uploadLocation;

    private final static String uploadPath = "/uploads";
    private final static String profilePicturePath = uploadPath + "/profilePicture";

    public UploadServiceTest() throws IOException {
        this.uploadLocation = Paths.get(uploadPath).toAbsolutePath().normalize();

        Files.createDirectories(this.uploadLocation);
    }

    private void SanityCheck(MultipartFile file) {
        // * Sanity check
        if (file.isEmpty())
            throw new ApiException(ExceptionEnum.EMPTY_FILE);
        // * if deploy on production, you have to make more filter
        if (StringUtils.cleanPath(file.getOriginalFilename()).contains(".."))
            throw new ApiException(ExceptionEnum.INVALID_FILE_NAME);
    }

    @Test
    public void SaveFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "shit.jpg",
                "image/png",
                new FileInputStream("./src/test/java/kr/pwner/fakegram/image/image.jpg")
        );
        // * Sanity check
        if (file.isEmpty())
            throw new ApiException(ExceptionEnum.EMPTY_FILE);
        // * if deploy on production, you have to make more filter
        if (StringUtils.cleanPath(file.getOriginalFilename()).contains(".."))
            throw new ApiException(ExceptionEnum.INVALID_FILE_NAME);

        // * for security and identify the file
        String fileNameAndExtension = UUID.randomUUID().toString() + "." +
                FilenameUtils.getExtension(file.getOriginalFilename());
        Path filePath = this.uploadLocation.resolve(fileNameAndExtension);

        // * Save given file - don't have any attribute
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ApiException(ExceptionEnum.COULD_NOT_SAVE_THE_FILE);
        }
    }
}
