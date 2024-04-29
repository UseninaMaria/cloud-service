package ru.netology.diplomcloud.facade;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomcloud.dto.request.EditFileNameRequestDto;
import ru.netology.diplomcloud.dto.response.FileListResponseDto;
import ru.netology.diplomcloud.dto.response.ResponseMessageDto;
import ru.netology.diplomcloud.entity.Cloud;
import ru.netology.diplomcloud.service.CloudService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Setter
@RequiredArgsConstructor
public class CloudFacade {
    private final CloudService cloudService;

    public List<FileListResponseDto> getFileList(String authToken, Integer limit) {
        List<Cloud> clouds = cloudService.getFileList(authToken, limit);
        List<FileListResponseDto> fileListResponses = new ArrayList<>();

        for (Cloud cloud : clouds) {
            fileListResponses.add(new FileListResponseDto(cloud.getFileName(), cloud.getFileSize()));
        }
        return fileListResponses;
    }

    public ResponseMessageDto uploadFile(String authToken, String filename, MultipartFile file) throws IOException {
        cloudService.uploadFile(authToken, filename, file);
        return new ResponseMessageDto("Success upload");
    }

    public ResponseMessageDto editFileName(String authToken, String filename,
                                           EditFileNameRequestDto editFileNameRequest) {
        cloudService.editFileName(authToken, filename, editFileNameRequest);
        return new ResponseMessageDto("Success edit file" + filename);
    }

    public ResponseMessageDto deleteFile(String authToken, String filename) {
        cloudService.deleteFile(authToken, filename);
        return new ResponseMessageDto("Delete success" + filename);
    }

    public byte[] downloadFile(String authToken, String filename) {
        return cloudService.downloadFile(authToken, filename);
    }
}
