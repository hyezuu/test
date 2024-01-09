package com.ssdam.like.repository;

import com.ssdam.comment.entity.Comment;
import com.ssdam.like.entity.Like;
import com.ssdam.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberAndComment(Member member, Comment comment);
    void deleteByMemberAndComment(Member member, Comment comment);
}
