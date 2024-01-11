package com.ssdam.bookmark.service;

import com.ssdam.bookmark.entity.Bookmark;
import com.ssdam.bookmark.repository.BookmarkRepository;
import com.ssdam.member.entity.Member;
import com.ssdam.member.service.MemberService;
import com.ssdam.party.entity.Party;
import com.ssdam.party.service.PartyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookmarkService {
    private final PartyService partyService;
    private final MemberService memberService;
    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(PartyService partyService, MemberService memberService, BookmarkRepository bookmarkRepository) {
        this.partyService = partyService;
        this.memberService = memberService;
        this.bookmarkRepository = bookmarkRepository;
    }

    public void toggleBookmark(long partyId, long memberId){
        Member member = memberService.findMember(memberId);
        Party party = partyService.findParty(partyId);
        if(!isPartyBookmarkedByUser(memberId, partyId)){
            party.setBookmarkCount(party.getBookmarkCount()+1);
            bookmarkRepository.save(new Bookmark(member,party));
        }
        else {
            party.setBookmarkCount(party.getBookmarkCount()-1);
            bookmarkRepository.deleteByMemberAndParty(member,party);
        }
    }
    public boolean isPartyBookmarkedByUser(long memberId, long partyId) {
        Member member = memberService.findMember(memberId);
        Party party = partyService.findParty(partyId);
        return bookmarkRepository.existsByMemberAndParty(member,party);
    }
}
