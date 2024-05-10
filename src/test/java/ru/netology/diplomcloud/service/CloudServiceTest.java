package ru.netology.diplomcloud.service;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.diplomcloud.authorization.UsernamePasswordAuthentication;
import ru.netology.diplomcloud.dto.request.EditFileNameRequestDto;
import ru.netology.diplomcloud.entity.Cloud;
import ru.netology.diplomcloud.entity.User;
import ru.netology.diplomcloud.exceptions.InternalServerException;
import ru.netology.diplomcloud.exceptions.NotFoundException;
import ru.netology.diplomcloud.repository.CloudRepository;
import ru.netology.diplomcloud.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.netology.diplomcloud.Utils.FILE_NAME;
import static ru.netology.diplomcloud.Utils.FILE_NAME_2;
import static ru.netology.diplomcloud.Utils.FILE_NAME_WRONG;
import static ru.netology.diplomcloud.Utils.FILE_SIZE;
import static ru.netology.diplomcloud.Utils.USER_NAME;

@SpringBootTest
public class CloudServiceTest {
    @Autowired
    CloudService cloudService;
    @Autowired
    CloudRepository cloudRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByUsername(USER_NAME);
        cloudRepository.save(
            new Cloud(FILE_NAME, FILE_SIZE, FILE_NAME.getBytes(), user));

        Authentication authentication = new UsernamePasswordAuthentication(USER_NAME, null, null);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        cloudRepository.deleteAll();
    }

    @Test
    public void uploadFileError() {
        assertThrows(InternalServerException.class, () -> cloudService.uploadFile(FILE_NAME, null));
    }

    @Test
    public void getFileList() {
        List<Cloud> fileList = cloudService.getFileList(3);

        assertFalse(fileList.isEmpty());
        assertEquals(fileList.size(), 1);
    }

    @Test
    public void downloadFile() {
        byte[] bytesActual = FILE_NAME.getBytes();

        byte[] bytesExpected = cloudService.downloadFile(FILE_NAME);

        assertTrue(bytesExpected.length > 0);
        assertArrayEquals(bytesExpected, bytesActual);
    }

    @Test
    public void downloadFileError() {
        assertThrows(NotFoundException.class, () -> cloudService.downloadFile(FILE_NAME_WRONG));
    }

    @Test
    public void deleteFile() {
        cloudService.deleteFile(FILE_NAME);

        User user = userRepository.findByUsername(USER_NAME);
        Optional<Cloud> cloud = cloudRepository.findByUserAndFileName(user, FILE_NAME);

        assertFalse(cloud.isPresent());
    }

    @Test
    public void editFileName() {
        cloudService.editFileName(FILE_NAME, new EditFileNameRequestDto(FILE_NAME_2));

        User user = userRepository.findByUsername(USER_NAME);
        Optional<Cloud> cloud = cloudRepository.findByUserAndFileName(user, FILE_NAME_2);

        assertTrue(cloud.isPresent());
        assertEquals(cloud.get().getFileName(), FILE_NAME_2);
        assertEquals(cloud.get().getUser().getUsername(), user.getUsername());
    }
}
