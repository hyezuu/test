package com.ssdam.comment.service;

import com.ssdam.comment.entity.Comment;
import com.ssdam.comment.repository.CommentRepository;
import com.ssdam.exception.BusinessLogicException;
import com.ssdam.exception.ExceptionCode;
import com.ssdam.utils.CustomBeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final CustomBeanUtils<Comment> beanUtils;

    public CommentService(CommentRepository commentRepository, CustomBeanUtils<Comment> beanUtils) {
        this.commentRepository = commentRepository;
        this.beanUtils = beanUtils;
    }
    //party 상태변경(모집종료) 시 댓글작성 불가 로직 필요
    public Comment createComment (Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment) {
        Comment foundComment
                = findVerifiedComment(comment.getCommentId());
        beanUtils.copyNonNullProperties(comment, foundComment);
        return foundComment;
    }

    @Transactional(readOnly = true)
    public Comment findComment(long commentId) {
        return findVerifiedComment(commentId);
    }

    @Transactional(readOnly = true)
    public Page<Comment> findComments (int page, int size) {
        return commentRepository.findAll(PageRequest.of(page, size, Sort.by("commentId").descending()));
    }

    //멤버가 작성한 모든 댓글 조회
    @Transactional(readOnly = true)
    public Page<Comment> findCommentsByMember (long memberId, int page, int size) {
        List<Comment> comments = commentRepository.findByMember_MemberId(memberId);
        Page<Comment> pageComments =
                new PageImpl<>(comments,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()),comments.size());
        return pageComments;
    }

    //파티에 있는 모든 댓글 조회, 좋아요순 구현 필요
    @Transactional(readOnly = true)
    public Page<Comment> findCommentsByParty (long partyId, int page, int size) {
        List<Comment> comments = commentRepository.findByParty_PartyId(partyId);
        Page<Comment> pageComments =
                new PageImpl<>(comments,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()),comments.size());
        return pageComments;
    }

    public void deleteComment (long commentId) {
        commentRepository.delete(findVerifiedComment(commentId));
    }

    private Comment findVerifiedComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }
}
