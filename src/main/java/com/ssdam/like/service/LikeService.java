package com.ssdam.like.service;

import com.ssdam.comment.entity.Comment;
import com.ssdam.comment.service.CommentService;
import com.ssdam.like.entity.Like;
import com.ssdam.like.repository.LikeRepository;
import com.ssdam.member.entity.Member;
import com.ssdam.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {
    private final CommentService commentService;
    private final MemberService memberService;
    private final LikeRepository likeRepository;

    public LikeService(CommentService commentService, MemberService memberService, LikeRepository likeRepository) {
        this.commentService = commentService;
        this.memberService = memberService;
        this.likeRepository = likeRepository;
    }

    public void toggleLike(long commentId, long memberId){
        Comment comment = commentService.findComment(commentId);
        Member member = memberService.findMember(memberId);
        if(!isCommentLikedByUser(commentId,memberId)){          //member가 comment에 좋아요를 누른적 없는 경우
            comment.setLikeCount(comment.getLikeCount()+1);
            likeRepository.save(new Like(member, comment));
        }
        else{
            comment.setLikeCount(comment.getLikeCount()-1);
            likeRepository.deleteByMemberAndComment(member, comment);//member가 comment에 좋아요를 눌렀던 경우
        }
    }
    public boolean isCommentLikedByUser(long commentId, long memberId) {
        Comment comment = commentService.findComment(commentId);
        Member member = memberService.findMember(memberId);
        return likeRepository.existsByMemberAndComment(member, comment);
    }
}
