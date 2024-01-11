package com.ssdam.bookmark.repository;

import com.ssdam.bookmark.entity.Bookmark;
import com.ssdam.member.entity.Member;
import com.ssdam.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByMemberAndParty(Member member, Party party);

    void deleteByMemberAndParty(Member member, Party party);
}
