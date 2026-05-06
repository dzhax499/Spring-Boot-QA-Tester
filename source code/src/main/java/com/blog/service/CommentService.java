package com.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.repository.CommentJpaRepository;
import com.blog.vo.Comment;

@Service
public class CommentService {

	// 1. Perbaikan: Constructor Injection dengan 'final'
	private final CommentJpaRepository commentJpaRepository;

	@Autowired
	public CommentService(CommentJpaRepository commentJpaRepository) {
		this.commentJpaRepository = commentJpaRepository;
	}

	public boolean saveComment(Comment comment) {
		// 2. Perbaikan: Disederhanakan menjadi satu baris ekspresi boolean
		return commentJpaRepository.save(comment) != null;
	}

	public List<Comment> getCommentList(Long postId) {
		// 3. Perbaikan: Langsung return list-nya
		return commentJpaRepository.findAllByPostIdOrderByRegDateDesc(postId);
	}

	public List<Comment> searchCommentList(Long postId, String query) {
		// PERBAIKAN FATAL: Mengubah kata 'Comment' menjadi 'Content' pada pemanggilan
		// fungsi
		return commentJpaRepository.findByPostIdAndContentContainingOrderByRegDateDesc(postId, query);
	}

	public Comment getComment(Long id) {
		// 3. Perbaikan: Langsung return object-nya
		return commentJpaRepository.findOneById(id);
	}

	public boolean deleteComment(Long id) {
		Comment result = commentJpaRepository.findOneById(id);

		// Menambahkan kurung kurawal agar lebih sesuai dengan clean code standard
		if (result == null) {
			return false;
		}

		commentJpaRepository.deleteById(id);
		return true;
	}
}