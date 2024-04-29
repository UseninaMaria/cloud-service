package ru.netology.diplomcloud.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.diplomcloud.authorization.UsernamePasswordAuthentication;
import ru.netology.diplomcloud.dto.request.EditFileNameRequestDto;
import ru.netology.diplomcloud.dto.response.FileListResponseDto;
import ru.netology.diplomcloud.facade.CloudFacade;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CloudController.class)
@AutoConfigureMockMvc
public class CloudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudFacade cloudFacade;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAllFiles() throws Exception {
        setAuth();
        List<FileListResponseDto> list = List.of(FileListResponseDto.builder()
            .filename("testName")
            .size(10L)
            .build());

        when(cloudFacade.getFileList("test", any())).thenReturn(list);

        var result = mockMvc.perform(get("/list").param("limit", String.valueOf(1)))
            .andExpect(status().isOk())
            .andReturn();
        var resultArray =
            objectMapper.readValue(result.getResponse().getContentAsString(), FileListResponseDto[].class);

        assertEquals(resultArray.length, 1);
        assertEquals(resultArray[0].getFilename(), list.get(0).getFilename());
        assertEquals(resultArray[0].getSize(), list.get(0).getSize());
    }

    @Test
    void downloadFile() throws Exception {
        setAuth();
        byte[] bytes = "testName".getBytes();

        when(cloudFacade.downloadFile("test", any())).thenReturn(bytes);

        var result = mockMvc.perform(get("/file").param("filename", "testName"))
            .andExpect(status().isOk())
            .andReturn();
        var resultBytes = result.getResponse().getContentAsByteArray();

        assertArrayEquals(resultBytes, bytes);
    }

    @Test
    void uploadFile() throws Exception {
        setAuth();

        doNothing().when(cloudFacade).uploadFile("test", any(), any());

        mockMvc.perform(post("/file").param("filename", "testName2"))
            .andExpect(status().isOk());

    }

    @Test
    void deleteFile() throws Exception {
        setAuth();

        doNothing().when(cloudFacade).deleteFile("test", any());

        mockMvc.perform(delete("/file").param("filename", "testName2"))
            .andExpect(status().isOk());
    }

    @Test
    void editFileName() throws Exception {
        setAuth();

        doNothing().when(cloudFacade).editFileName("test", any(), any());
        var body = objectMapper.writeValueAsString(new EditFileNameRequestDto("testName"));

        mockMvc.perform(put("/file")
                .param("filename", "testName2")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private void setAuth() {
        Authentication authentication = new UsernamePasswordAuthentication("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

