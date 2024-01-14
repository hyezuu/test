package com.ssdam.comment.repository;

import com.ssdam.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByMember_MemberId(Long memberId, Pageable pageable);
    Page<Comment> findByParty_PartyId(Long partyId, Pageable pageable);
}
