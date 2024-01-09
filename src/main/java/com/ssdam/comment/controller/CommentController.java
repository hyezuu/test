package com.ssdam.comment.controller;

import com.ssdam.comment.dto.CommentDto;
import com.ssdam.comment.entity.Comment;
import com.ssdam.comment.mapper.CommentMapper;
import com.ssdam.comment.service.CommentService;
import com.ssdam.dto.MultiResponseDto;
import com.ssdam.dto.SingleResponseDto;
import com.ssdam.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
public class CommentController {
    private final static String COMMENT_DEFAULT_URL = "/v1/comments";
    private final CommentService commentService;
    private final CommentMapper mapper;


    public CommentController(CommentService commentService, CommentMapper mapper) {
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @RequestMapping(value = "/v1/comments", method = RequestMethod.POST)
    public ResponseEntity postComment(@RequestBody @Valid CommentDto.Post requestBody) {
        Comment comment = mapper.commentPostDtoToComment(requestBody);
        Comment createdComment = commentService.createComment(comment);

        URI location = UriCreator.createUri(COMMENT_DEFAULT_URL, createdComment.getCommentId());
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/v1/comments/{comment-id}", method = RequestMethod.PATCH)
    public ResponseEntity patchComment(@PathVariable("comment-id") @Positive long commentId,
                                       @RequestBody @Valid CommentDto.Patch requestBody) {
        requestBody.setCommentId(commentId);
        Comment comment = mapper.commentPatchDtoToComment(requestBody);
        Comment updatedComment = commentService.updateComment(comment);

        return new ResponseEntity(
                new SingleResponseDto<>(mapper.commentToCommentResponse(updatedComment)),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/comments/{comment-id}", method = RequestMethod.GET)
    public ResponseEntity getComment(@PathVariable("comment-id") @Positive long commentId) {
        Comment comment = commentService.findComment(commentId);

        return new ResponseEntity(
                new SingleResponseDto<>(mapper.commentToCommentResponse(comment)),
                HttpStatus.OK);
    }
    //모든 댓글 조회
    @RequestMapping(value = "/v1/comments", method = RequestMethod.GET)
    public ResponseEntity getComments(@Positive @RequestParam int page,
                                      @Positive @RequestParam int size) {
        Page<Comment> pageComments = commentService.findComments(page - 1, size);
        List<Comment> comments = pageComments.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.commentsToCommentResponses(comments), pageComments),
                HttpStatus.OK);
    }
    //특정멤버가 작성한 모든 댓글 조회
    @RequestMapping(value = "/v1/comments", method = RequestMethod.GET, params = {"memberId"})
    public ResponseEntity getCommentsByMember(@Positive @RequestParam long memberId,
                                              @Positive @RequestParam int page,
                                              @Positive @RequestParam int size) {
        Page<Comment> pageComments = commentService.findCommentsByMember(memberId, page - 1, size);
        List<Comment> comments = pageComments.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.commentsToCommentResponses(comments), pageComments),
                HttpStatus.OK);
    }
    //특정파티에 존재하는 모든 댓글 조회
    @RequestMapping(value = "/v1/comments", method = RequestMethod.GET, params = {"partyId"})
    public ResponseEntity getCommentsByParty(@Positive @RequestParam long partyId,
                                             @Positive @RequestParam int page,
                                             @Positive @RequestParam int size) {
        Page<Comment> pageComments = commentService.findCommentsByParty(partyId, page - 1, size);
        List<Comment> comments = pageComments.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.commentsToCommentResponses(comments), pageComments),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/comments/{comment-id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteComment(@PathVariable("comment-id") @Positive long commentId) {
        commentService.deleteComment(commentId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}