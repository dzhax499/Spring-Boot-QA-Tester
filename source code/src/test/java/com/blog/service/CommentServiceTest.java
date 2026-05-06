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

import com.blog.repository.CommentJpaRepository;
import com.blog.vo.Comment;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentJpaRepository commentJpaRepository;

    @BeforeEach
    void setUp() {
        // MockitoExtension handles initialization
    }

    @Test
    void testSaveComment_Success() {
        Comment comment = new Comment(1L, "user", "nice post");
        when(commentJpaRepository.save(any(Comment.class))).thenReturn(comment);

        boolean result = commentService.saveComment(comment);

        assertTrue(result);
        verify(commentJpaRepository, times(1)).save(comment);
    }

    @Test
    void testSaveComment_Fail() {
        Comment comment = new Comment(1L, "user", "nice post");
        when(commentJpaRepository.save(any(Comment.class))).thenReturn(null);

        boolean result = commentService.saveComment(comment);

        assertFalse(result);
        verify(commentJpaRepository, times(1)).save(comment);
    }

    @Test
    void testGetCommentList() {
        Long postId = 1L;
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(postId, "user1", "comment1"));
        when(commentJpaRepository.findAllByPostIdOrderByRegDateDesc(postId)).thenReturn(comments);

        List<Comment> result = commentService.getCommentList(postId);

        assertEquals(1, result.size());
        verify(commentJpaRepository, times(1)).findAllByPostIdOrderByRegDateDesc(postId);
    }

    @Test
    void testSearchCommentList() {
        Long postId = 1L;
        String query = "good";
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(postId, "user", "very good"));

        // PERBAIKAN 1: Menggunakan fungsi dengan nama 'Content'
        when(commentJpaRepository.findByPostIdAndContentContainingOrderByRegDateDesc(postId, query))
                .thenReturn(comments);

        List<Comment> result = commentService.searchCommentList(postId, query);

        assertEquals(1, result.size());

        // PERBAIKAN 2: Menggunakan getter 'getContent()'
        assertTrue(result.get(0).getContent().contains(query));

        // PERBAIKAN 3: Memverifikasi pemanggilan fungsi dengan nama 'Content'
        verify(commentJpaRepository, times(1)).findByPostIdAndContentContainingOrderByRegDateDesc(postId, query);
    }

    @Test
    void testGetComment() {
        Long id = 1L;
        Comment comment = new Comment(1L, "user", "comment");
        when(commentJpaRepository.findOneById(id)).thenReturn(comment);

        Comment result = commentService.getComment(id);

        assertEquals(comment, result);
        verify(commentJpaRepository, times(1)).findOneById(id);
    }

    @Test
    void testDeleteComment_Success() {
        Long id = 1L;
        Comment comment = new Comment(1L, "user", "comment");
        when(commentJpaRepository.findOneById(id)).thenReturn(comment);

        boolean result = commentService.deleteComment(id);

        assertTrue(result);
        verify(commentJpaRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteComment_NotFound() {
        Long id = 1L;
        when(commentJpaRepository.findOneById(id)).thenReturn(null);

        boolean result = commentService.deleteComment(id);

        assertFalse(result);
        verify(commentJpaRepository, times(0)).deleteById(id);
    }
}