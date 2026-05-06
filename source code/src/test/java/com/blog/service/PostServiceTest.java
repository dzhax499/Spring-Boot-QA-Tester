package com.blog.service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.repository.PostJpaRepository;
import com.blog.repository.PostRepository;
import com.blog.vo.Post;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // MockitoExtension handles initialization
    }

    @Test
    void testGetPost() {
        // Arrange
        Long id = 1L;
        Post post = new Post(id, "user", "title", "content");
        when(jpaRepository.findOneById(id)).thenReturn(post);

        // Act
        Post result = postService.getPost(id);

        // Assert
        assertEquals(id, result.getId());
        assertEquals("user", result.getUser());
        verify(jpaRepository, times(1)).findOneById(id);
    }

    @Test
    void testGetPosts() {
        // Arrange
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user1", "title1", "content1"));
        posts.add(new Post(2L, "user2", "title2", "content2"));
        when(jpaRepository.findAllByOrderByUpdtDateDesc()).thenReturn(posts);

        // Act
        List<Post> result = postService.getPosts();

        // Assert
        assertEquals(2, result.size());
        verify(jpaRepository, times(1)).findAllByOrderByUpdtDateDesc();
    }

    @Test
    void testSavePost_Success() {
        // Arrange
        Post post = new Post("user", "title", "content");
        when(jpaRepository.save(any(Post.class))).thenReturn(post);

        // Act
        boolean result = postService.savePost(post);

        // Assert
        assertTrue(result);
        verify(jpaRepository, times(1)).save(post);
    }

    @Test
    void testSavePost_Fail() {
        // Arrange
        Post post = new Post("user", "title", "content");
        when(jpaRepository.save(any(Post.class))).thenReturn(null);

        // Act
        boolean result = postService.savePost(post);

        // Assert
        assertFalse(result);
        verify(jpaRepository, times(1)).save(post);
    }

    @Test
    void testDeletePost_Success() {
        // Arrange
        Long id = 1L;
        Post post = new Post(id, "user", "title", "content");
        when(jpaRepository.findOneById(id)).thenReturn(post);

        // Act
        boolean result = postService.deletePost(id);

        // Assert
        assertTrue(result);
        verify(jpaRepository, times(1)).findOneById(id);
        verify(jpaRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeletePost_NotFound() {
        // Arrange
        Long id = 1L;
        when(jpaRepository.findOneById(id)).thenReturn(null);

        // Act
        boolean result = postService.deletePost(id);

        // Assert
        assertFalse(result);
        verify(jpaRepository, times(1)).findOneById(id);
        verify(jpaRepository, times(0)).deleteById(id);
    }

    @Test
    void testUpdatePost_Success() {
        // Arrange
        Post existingPost = new Post(1L, "user", "old title", "old content");
        Post updateParam = new Post(1L, "new title", "new content");
        when(jpaRepository.findOneById(1L)).thenReturn(existingPost);

        // Act
        boolean result = postService.updatePost(updateParam);

        // Assert
        assertTrue(result);
        assertEquals("new title", existingPost.getTitle());
        assertEquals("new content", existingPost.getContent());
        verify(jpaRepository, times(1)).save(existingPost);
    }

    @Test
    void testUpdatePost_NotFound() {
        // Arrange
        Post updateParam = new Post(1L, "new title", "new content");
        when(jpaRepository.findOneById(1L)).thenReturn(null);

        // Act
        boolean result = postService.updatePost(updateParam);

        // Assert
        assertFalse(result);
        verify(jpaRepository, times(0)).save(any(Post.class));
    }

    @Test
    void testSearchPostByTitle() {
        // Arrange
        String query = "Spring";
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user", "Spring Boot Guide", "content"));
        when(jpaRepository.findByTitleContainingOrderByUpdtDateDesc(query)).thenReturn(posts);

        // Act
        List<Post> result = postService.searchPostByTitle(query);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().contains(query));
        verify(jpaRepository, times(1)).findByTitleContainingOrderByUpdtDateDesc(query);
    }

    @Test
    void testSearchPostByContent() {
        // Arrange
        String query = "testing";
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1L, "user", "Title", "We are testing software quality."));
        when(jpaRepository.findByContentContainingOrderByUpdtDateDesc(query)).thenReturn(posts);

        // Act
        List<Post> result = postService.searchPostByContent(query);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getContent().contains(query));
        verify(jpaRepository, times(1)).findByContentContainingOrderByUpdtDateDesc(query);
    }
}
