package ru.netology.diplomcloud.service;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomcloud.dto.request.EditFileNameRequestDto;
import ru.netology.diplomcloud.entity.Cloud;
import ru.netology.diplomcloud.entity.User;
import ru.netology.diplomcloud.exceptions.InternalServerException;
import ru.netology.diplomcloud.exceptions.NotFoundException;
import ru.netology.diplomcloud.repository.CloudRepository;
import ru.netology.diplomcloud.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.netology.diplomcloud.util.AppConstant.BEARER;
import static ru.netology.diplomcloud.util.AppConstant.USERNAME;

@Slf4j
@RequiredArgsConstructor
@Service
public class CloudService {
    private final UserRepository userRepository;
    private final CloudRepository cloudRepository;
    private final SecurityService securityService;

    public void uploadFile(String authToken, String filename, MultipartFile file) throws IOException {
        log.debug("changeAccountBalanceCashTransaction() - start. AuthToken = {}, filename = {}, file = {}",
            authToken, filename, file);
        Cloud cloud;
        try {
            User user = getUserByAuthToken(authToken);
            cloud = new Cloud(filename, file.getSize(), file.getBytes(), user);
            cloudRepository.save(cloud);
            log.debug("uploadFile() - end. Cloud = {}", cloud);
        } catch (Exception e) {
            throw new InternalServerException("Error upload file " + e.getMessage());
        }
    }

    public List<Cloud> getFileList(String authToken, Integer limit) {
        log.debug("getFileList() - start. AuthToken = {}, limit = {}",
            authToken, limit);
        List<Cloud> list;
        try {
            User user = getUserByAuthToken(authToken);
            list = cloudRepository.findAllByUser(user, Limit.of(limit));
            log.debug("getFileList() - end. List = {}", list);
            return list;
        } catch (Exception e) {
            throw new InternalServerException("Error getting file list " + e.getMessage());
        }
    }

    public byte[] downloadFile(String authToken, String filename) {
        log.debug("downloadFile() - start. AuthToken = {}, filename = {}",
            authToken, filename);
        try {
            User user = getUserByAuthToken(authToken);
            Optional<Cloud> file = cloudRepository.findByUserAndFileName(user, filename);
            if (file.isPresent()) {
                log.debug("downloadFile() - end. File = {}", file);
                return file.get().getFileContent();
            } else {
                throw new NotFoundException(String.format("File with filename " + filename + " is not found"));
            }
        } catch (Exception e) {
            throw new InternalServerException("Error download file " + e.getMessage());
        }

    }

    @Transactional
    public void deleteFile(String authToken, String filename) {
        log.debug("deleteFile() - start. AuthToken = {}, filename = {}",
            authToken, filename);
        try {
            User user = getUserByAuthToken(authToken);
            cloudRepository.deleteByUserAndFileName(user, filename);
            log.debug("deleteFile() - end.");
        } catch (Exception e) {
            throw new InternalServerException("Error delete file " + e.getMessage());
        }
    }

    public void editFileName(String authToken, String filename, EditFileNameRequestDto editFileNameRequest) {
        log.debug("editFileName() - start. AuthToken = {}, filename = {}, editFileNameRequest = {}",
            authToken, filename, editFileNameRequest);
        try {
            User user = getUserByAuthToken(authToken);
            Optional<Cloud> file = cloudRepository.findByUserAndFileName(user, filename);
            if (file.isPresent()) {
                file.get().setFileName(editFileNameRequest.getFilename());
                cloudRepository.save(file.get());
                log.debug("editFileName() - end. File = {}", file);
            } else {
                throw new NotFoundException(String.format("File with filename " + filename + " is not found"));
            }
        } catch (Exception e) {
            throw new InternalServerException("Error edit file " + e.getMessage());
        }
    }

    private User getUserByAuthToken(String authToken) {
        log.debug("getUserByAuthToken() - start. AuthToken = {}",
            authToken);
        if (authToken.startsWith(BEARER)) {
            Claims claims = securityService.getClaims(authToken.replace(BEARER, "").trim());
            String username = String.valueOf(claims.get(USERNAME));
            User user = userRepository.findByUsername(username);
            if (user != null) {
                log.debug("getUserByAuthToken() - end. User = {}", user);
                return user;
            } else {
                throw new NotFoundException(String.format("User with username " + username + " is not found"));
            }
        }
        return null;
    }
}
