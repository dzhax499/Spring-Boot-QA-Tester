package com.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blog.service.PostService;

@WebMvcTest(PostController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    void testHandleMethodArgumentNotValid_ReturnsBadRequestWithErrors() throws Exception {
        // Kirim body yang tidak valid: semua field kosong → memicu @NotBlank validation
        String invalidBody = "{\"user\":\"\",\"title\":\"\",\"content\":\"\"}";

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testHandleMethodArgumentNotValid_ContainsFieldErrors() throws Exception {
        // Hanya user yang kosong → harus ada field error untuk "user"
        String invalidBody = "{\"user\":\"\",\"title\":\"Some Title\",\"content\":\"Some Content\"}";

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.user").exists());
    }
}
