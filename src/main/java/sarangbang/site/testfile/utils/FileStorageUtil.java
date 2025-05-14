package sarangbang.site.testfile.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileStorageUtil {

    private static final String FOLDER_NAME = "videos";
    private Path uploadPath;

    /**
     * 서버 시작 시 폴더 존재 확인 및 생성
     */
    @PostConstruct
    public void init() {
        String basePath = System.getProperty("user.dir"); // 현재 실행 디렉토리
        this.uploadPath = Paths.get(basePath, FOLDER_NAME);
        File dir = uploadPath.toFile();
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                log.info("폴더 생성됨: {}", uploadPath.toString());
            } else {
                log.warn("폴더 생성 실패: {}", uploadPath.toString());
            }
        } else {
            log.info("기존 폴더 존재: {}", uploadPath.toString());
        }
    }

    /**
     * MultipartFile을 videos 폴더에 저장하고 경로 반환
     */
    public String saveVideoFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String savedFileName = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(savedFileName);

        multipartFile.transferTo(targetPath.toFile());
        log.info("저장된 파일: {}", targetPath.toString());

        return savedFileName;
    }

    /**
     * videos 폴더에 저장된 파일 이름 목록 반환
     */
    public List<String> getVideoFileList() {
        File folder = uploadPath.toFile();
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }
}