package com.ssdam.comment.repository;

import com.ssdam.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMember_MemberId(Long memberId);
    List<Comment> findByParty_PartyId(Long partyId);
}
