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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blog.service.PostService;
import com.blog.vo.Post;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    // ── GET /post ────────────────────────────────────────────────────────────

    @Test
    void testGetPost_ReturnsPost() throws Exception {
        Post post = new Post(1L, "user1", "title1", "content1");
        when(postService.getPost(1L)).thenReturn(post);

        mockMvc.perform(get("/post").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user").value("user1"));
    }

    // ── GET /posts ───────────────────────────────────────────────────────────

    @Test
    void testGetPosts_ReturnsList() throws Exception {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user1", "title1", "content1"));
        posts.add(new Post(2L, "user2", "title2", "content2"));
        when(postService.getPosts()).thenReturn(posts);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ── GET /posts/updtdate/asc ───────────────────────────────────────────────

    @Test
    void testGetPostsOrderByUpdtAsc_ReturnsList() throws Exception {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user1", "title1", "content1"));
        when(postService.getPostsOrderByUpdtAsc()).thenReturn(posts);

        mockMvc.perform(get("/posts/updtdate/asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ── GET /posts/regdate/desc ──────────────────────────────────────────────

    @Test
    void testGetPostsOrderByRegDesc_ReturnsList() throws Exception {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user1", "title1", "content1"));
        when(postService.getPostsOrderByRegDesc()).thenReturn(posts);

        mockMvc.perform(get("/posts/regdate/desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ── GET /posts/search/title ──────────────────────────────────────────────

    @Test
    void testSearchByTitle_ReturnsList() throws Exception {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user1", "Spring Boot Guide", "content1"));
        when(postService.searchPostByTitle("Spring")).thenReturn(posts);

        mockMvc.perform(get("/posts/search/title").param("query", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring Boot Guide"));
    }

    // ── GET /posts/search/content ────────────────────────────────────────────

    @Test
    void testSearchByContent_ReturnsList() throws Exception {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user1", "title1", "Java testing example"));
        when(postService.searchPostByContent("testing")).thenReturn(posts);

        mockMvc.perform(get("/posts/search/content").param("query", "testing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Java testing example"));
    }

    // ── POST /post ───────────────────────────────────────────────────────────

    @Test
    void testSavePost_Success_Returns200() throws Exception {
        when(postService.savePost(any(Post.class))).thenReturn(true);

        String body = "{\"user\":\"user1\",\"title\":\"title1\",\"content\":\"content1\"}";

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testSavePost_Fail_Returns500() throws Exception {
        when(postService.savePost(any(Post.class))).thenReturn(false);

        String body = "{\"user\":\"user1\",\"title\":\"title1\",\"content\":\"content1\"}";

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Fail"));
    }

    @Test
    void testSavePost_InvalidBody_Returns400() throws Exception {
        // user kosong → validation error
        String body = "{\"user\":\"\",\"title\":\"title1\",\"content\":\"content1\"}";

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /post ─────────────────────────────────────────────────────────

    @Test
    void testDeletePost_Success_Returns200() throws Exception {
        when(postService.deletePost(1L)).thenReturn(true);

        mockMvc.perform(delete("/post").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDeletePost_NotFound_Returns500() throws Exception {
        when(postService.deletePost(1L)).thenReturn(false);

        mockMvc.perform(delete("/post").param("id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Fail"));
    }

    // ── PUT /post ────────────────────────────────────────────────────────────

    @Test
    void testModifyPost_Success_Returns200() throws Exception {
        when(postService.updatePost(any(Post.class))).thenReturn(true);

        String body = "{\"id\":1,\"user\":\"user1\",\"title\":\"new title\",\"content\":\"new content\"}";

        mockMvc.perform(put("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testModifyPost_NotFound_Returns500() throws Exception {
        when(postService.updatePost(any(Post.class))).thenReturn(false);

        String body = "{\"id\":99,\"user\":\"user1\",\"title\":\"new title\",\"content\":\"new content\"}";

        mockMvc.perform(put("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Fail"));
    }

    @Test
    void testModifyPost_InvalidBody_Returns400() throws Exception {
        // title & content kosong → validation error
        String body = "{\"id\":1,\"user\":\"\",\"title\":\"\",\"content\":\"\"}";

        mockMvc.perform(put("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
