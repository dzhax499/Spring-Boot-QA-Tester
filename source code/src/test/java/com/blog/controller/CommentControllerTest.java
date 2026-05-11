package com.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blog.service.CommentService;
import com.blog.vo.Comment;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    // ── POST /comment ─────────────────────────────────────────────────────────

    @Test
    void testSaveComment_Success_Returns200() throws Exception {
        when(commentService.saveComment(any(Comment.class))).thenReturn(true);

        String body = "{\"postId\":1,\"user\":\"user1\",\"content\":\"Nice post!\"}";

        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testSaveComment_Fail_Returns500() throws Exception {
        when(commentService.saveComment(any(Comment.class))).thenReturn(false);

        String body = "{\"postId\":1,\"user\":\"user1\",\"content\":\"Nice post!\"}";

        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Fail"));
    }

    @Test
    void testSaveComment_InvalidBody_Returns400() throws Exception {
        // postId null, user & content kosong → validation error
        String body = "{\"postId\":null,\"user\":\"\",\"content\":\"\"}";

        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ── GET /comments ─────────────────────────────────────────────────────────

    @Test
    void testGetComments_ReturnsList() throws Exception {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1L, "user1", "comment1"));
        comments.add(new Comment(1L, "user2", "comment2"));
        when(commentService.getCommentList(1L)).thenReturn(comments);

        mockMvc.perform(get("/comments").param("post_id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ── GET /comment ──────────────────────────────────────────────────────────

    @Test
    void testGetComment_ReturnsComment() throws Exception {
        Comment comment = new Comment(1L, "user1", "nice post");
        when(commentService.getComment(1L)).thenReturn(comment);

        mockMvc.perform(get("/comment").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("user1"))
                .andExpect(jsonPath("$.content").value("nice post"));
    }

    // ── DELETE /comment ───────────────────────────────────────────────────────

    @Test
    void testDeleteComment_Success_Returns200() throws Exception {
        when(commentService.deleteComment(1L)).thenReturn(true);

        mockMvc.perform(delete("/comment").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDeleteComment_NotFound_Returns500() throws Exception {
        when(commentService.deleteComment(1L)).thenReturn(false);

        mockMvc.perform(delete("/comment").param("id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Fail"));
    }

    // ── GET /comments/search ──────────────────────────────────────────────────

    @Test
    void testSearchComments_ReturnsList() throws Exception {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1L, "user1", "very good post"));
        when(commentService.searchCommentList(1L, "good")).thenReturn(comments);

        mockMvc.perform(get("/comments/search")
                        .param("post_id", "1")
                        .param("query", "good"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("very good post"));
    }
}
