package com.blog.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.blog.mapper.PostMapper;
import com.blog.vo.Post;

@Repository
public class PostRepository {

	// 1. Perbaikan: Mengubah Field Injection menjadi Constructor Injection
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public PostRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Post findById(Long id) {
		String sql = "SELECT * FROM post WHERE id = ?";

		// 2. Perbaikan Tambahan: Inisialisasi PostMapper langsung di dalam parameter
		// (Clean Code)
		return this.jdbcTemplate.queryForObject(sql, new PostMapper(), id);
	}

	public List<Post> findPost() {
		String sql = "SELECT * FROM post ORDER BY updt_date DESC";
		return this.jdbcTemplate.query(sql, new PostMapper());
	}

	public List<Post> findPostOrderByUpdtDateAsc() {
		String sql = "SELECT * FROM post ORDER BY updt_date ASC";
		return this.jdbcTemplate.query(sql, new PostMapper());
	}

	public List<Post> findPostOrderByRegDateDesc() {
		String sql = "SELECT * FROM post ORDER BY reg_date DESC";
		return this.jdbcTemplate.query(sql, new PostMapper());
	}

	public List<Post> findPostLikeTitle(String query) {
		String sql = "SELECT * FROM post WHERE title LIKE ?";
		return this.jdbcTemplate.query(sql, new PostMapper(), '%' + query + '%');
	}

	public List<Post> findPostLikeContent(String query) {
		String sql = "SELECT * FROM post WHERE content LIKE ?";
		return this.jdbcTemplate.query(sql, new PostMapper(), '%' + query + '%');
	}

	public int savePost(Post post) {
		String sql = "INSERT INTO post(user, title, content, reg_date, updt_date) VALUES(?,?,?,?,?)";
		return jdbcTemplate.update(sql, post.getUser(), post.getTitle(), post.getContent(), post.getRegDate(),
				post.getUpdtDate());
	}
}