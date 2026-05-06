package com.blog.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.blog.service.PostService;
import com.blog.vo.Post;
import com.blog.vo.Result;

@RestController
public class PostController {

    // Sebaiknya logger juga dijadikan private static final
    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    // 1. Perbaikan: Mendefinisikan string yang berulang sebagai konstanta
    // (Constant)
    private static final String SUCCESS_MSG = "Success";
    private static final String FAIL_MSG = "Fail";

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    public Post getPost(@RequestParam("id") Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/posts")
    public List<Post> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/posts/updtdate/asc")
    public List<Post> getPostsOrderByUpdtAsc() {
        return postService.getPostsOrderByUpdtAsc();
    }

    @GetMapping("/posts/regdate/desc")
    public List<Post> getPostsOrderByRegDesc() {
        return postService.getPostsOrderByRegDesc();
    }

    @GetMapping("/posts/search/title")
    public List<Post> searchByTitle(@RequestParam("query") String query) {
        return postService.searchPostByTitle(query);
    }

    // for Exercise 4-4
    @GetMapping("/posts/search/content")
    public List<Post> searchByContent(@RequestParam("query") String query) {
        return postService.searchPostByContent(query);
    }

    @PostMapping("/post")
    public Object savePost(HttpServletResponse response, @Valid @RequestBody Post postParam) {
        Post post = new Post(
                HtmlUtils.htmlEscape(postParam.getUser()),
                HtmlUtils.htmlEscape(postParam.getTitle()),
                HtmlUtils.htmlEscape(postParam.getContent()));
        boolean isSuccess = postService.savePost(post);

        if (isSuccess) {
            return new Result(200, SUCCESS_MSG); // Menggunakan Konstanta
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Result(500, FAIL_MSG); // Menggunakan Konstanta
        }
    }

    @DeleteMapping("/post")
    public Object deletePost(HttpServletResponse response, @RequestParam("id") Long id) {
        boolean isSuccess = postService.deletePost(id);

        // 2. Perbaikan: Menggunakan format specifier {} alih-alih string concatenation
        // (+)
        log.info("id ::: {}", id);

        if (isSuccess) {
            return new Result(200, SUCCESS_MSG);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Result(500, FAIL_MSG);
        }
    }

    @PutMapping("/post")
    public Object modifyPost(HttpServletResponse response, @Valid @RequestBody Post postParam) {
        Post post = new Post(
                postParam.getId(),
                HtmlUtils.htmlEscape(postParam.getTitle()),
                HtmlUtils.htmlEscape(postParam.getContent()));
        boolean isSuccess = postService.updatePost(post);

        if (isSuccess) {
            return new Result(200, SUCCESS_MSG);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Result(500, FAIL_MSG);
        }
    }
}