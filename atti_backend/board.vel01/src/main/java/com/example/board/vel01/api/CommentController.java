package com.example.board.vel01.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.vel01.domain.Comment;
import com.example.board.vel01.domain.Post;
import com.example.board.vel01.domain.User;
import com.example.board.vel01.repository.CommentRepository;
import com.example.board.vel01.repository.PostRepository;
import com.example.board.vel01.repository.UserRepository;
import com.example.board.vel01.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {

	private final UserRepository userRepository;

	private final PostRepository postreRepository;

	private final CommentRepository commentRepository;

	private final CommentService commentService;

	// λκΈ μμ± = C
	@PostMapping
	public ResponseEntity<?> createComment(@AuthenticationPrincipal String nickName,
			@Valid @RequestBody Comment.Request req) {
		Comment comment = Comment.Request.toEntity(req);
		comment.setCreatedDate(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));

		Post findPost = postreRepository.findByPostId(comment.getPostSeq());
		User findUser = userRepository.findByNickName(nickName);
		comment.setUser(findUser);
		comment.setPost(findPost);
		comment.setNickName(nickName);
		comment.setPostSeq(findPost.getPostId());
		commentService.saveComment(comment);

		Comment.Response res = Comment.Response.toResponse(comment);
		return ResponseEntity.ok().body(res);
	}

	// λκΈ μ μ²΄ μ‘°ν = R
	@GetMapping
	public ResponseEntity<?> getCommentList(@RequestBody Comment.Request req) {

		List<Comment> comments = commentService.retrieveCommentList(req.getPostSeq());
		List<Comment.Response> commentList = Comment.Response.toResponseList(comments);

		return ResponseEntity.ok().body(commentList);
	}

	// νμ€νΈ μ©λ
	@GetMapping("/test")
	public void getuser(@RequestBody Comment.Request req) {
		Comment findComment = commentRepository.findByCommentId(req.getCommentId());
		System.out.println(" μ μ  μ λ³΄ νμΈ " + findComment.getUser());
	}

	// λκΈ μμ  = U
	@PutMapping
	public ResponseEntity<?> updateComment(@AuthenticationPrincipal String nickName,
			@Valid @RequestBody Comment.Request req) {

		Comment updatedComment = commentRepository.findByCommentId(req.getCommentId());

		if (updatedComment.getNickName().equals(nickName)) {

			commentService.updateComment(req);
			Comment.Response res = Comment.Response.toResponse(updatedComment);
			return ResponseEntity.ok().body(res);
		}

		return ResponseEntity.ok().body("ν΄λΉ λκΈ μμ±μλ§ μ΄ μμ²­μ ν  μ μμ΅λλ€.");
	}

	// λκΈ μ­μ  = D
	@DeleteMapping
	@Transactional
	public ResponseEntity<?> deleteComment(@AuthenticationPrincipal String nickName, @RequestBody Comment.Request req) {

		Comment findComment = commentRepository.findByCommentId(req.getCommentId());

		if (findComment.getNickName().equals(nickName)) {

//		Comment comment = Comment.Request.toEntity(req);

			commentService.deleteComment(findComment);

			List<Comment> updatedComments = commentService.retrieveCommentList(findComment.getPostSeq());

			List<Comment.Response> res = Comment.Response.toResponseList(updatedComments);

			return ResponseEntity.ok().body(res);
		}

		return ResponseEntity.badRequest().body("ν΄λΉ λκΈ μμ±μλ§ μ΄ μμ²­μ ν  μ μμ΅λλ€.");
	}
}