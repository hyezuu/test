package com.ssdam.like.service;

import com.ssdam.comment.entity.Comment;
import com.ssdam.comment.service.CommentService;
import com.ssdam.like.entity.Like;
import com.ssdam.like.repository.LikeRepository;
import com.ssdam.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {
    private final CommentService commentService;
    private final LikeRepository likeRepository;

    public LikeService(CommentService commentService, LikeRepository likeRepository) {
        this.commentService = commentService;
        this.likeRepository = likeRepository;
    }

    public void addLike(long commentId, Member member){
        Comment comment = commentService.findComment(commentId);
        if(!likeRepository.existsByMemberAndComment(member, comment)){//member가 comment에 좋아요를 누른적 없는경우
            comment.setLikeCount(comment.getLikeCount()+1);
            likeRepository.save(new Like(member, comment));
        }
        else{
            comment.setLikeCount(comment.getLikeCount()-1);
            likeRepository.deleteByMemberAndComment(member, comment);//member가 comment에 좋아요를 누른 경우
        }
    }
}
