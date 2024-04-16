package ru.netology.diplomcloud.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomcloud.service.CloudService;
import ru.netology.diplomcloud.dto.request.EditFileNameRequestDto;
import ru.netology.diplomcloud.dto.response.FileListResponseDto;

import java.io.IOException;
import java.util.List;

import static ru.netology.diplomcloud.util.AppConstant.AUTH_TOKEN;
import static ru.netology.diplomcloud.util.AppConstant.FILENAME;
import static ru.netology.diplomcloud.util.AppConstant.LIMIT;

@RestController
@RequiredArgsConstructor
public class CloudController {
    private final CloudService cloudService;

    @GetMapping("${myapp.alias.api.list}")
    public List<FileListResponseDto> getAllFiles(@RequestHeader(AUTH_TOKEN) String authToken,
                                                 @RequestParam(LIMIT) Integer limit) {
        return cloudService.getFileList(authToken, limit);
    }

    @GetMapping("${myapp.alias.api.file}")
    public ResponseEntity<Resource> downloadFileFromCloud(@RequestHeader(AUTH_TOKEN) String authToken,
                                                 @RequestParam(FILENAME) String filename) {
        byte[] file = cloudService.downloadFile(authToken, filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PostMapping("${myapp.alias.api.file}")
    public ResponseEntity<Object> uploadFile(@RequestHeader(AUTH_TOKEN) String authToken,
                                        @RequestParam(FILENAME) String filename,
                                        MultipartFile file) throws IOException {
        cloudService.uploadFile(authToken, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("${myapp.alias.api.file}")
    public ResponseEntity<Object> editFileName(@RequestHeader(AUTH_TOKEN) String authToken,
                                               @RequestParam(FILENAME) String filename,
                                               @RequestBody EditFileNameRequestDto editFileNameRequest) {
        cloudService.editFileName(authToken, filename, editFileNameRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("${myapp.alias.api.file}")
    public ResponseEntity<Object> deleteFile(@RequestHeader(AUTH_TOKEN) String authToken,
                                        @RequestParam(FILENAME) String filename) {
        cloudService.deleteFile(authToken, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
