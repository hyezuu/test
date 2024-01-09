package com.ssdam.party.repository;

import com.ssdam.member.entity.Member;
import com.ssdam.party.entity.Party;
import com.ssdam.party.entity.PartyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    boolean existsByMemberAndParty(Member member, Party party);
    void deleteByMemberAndParty(Member member, Party party);
}
