package com.ssdam.bookmark.controller;

import com.ssdam.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/parties/{party-id}")
    public ResponseEntity toggleBookmarkToParty(@PathVariable("party-id")@Positive long partyId,
                                                @RequestParam @Positive long memberId){
        bookmarkService.toggleBookmark(memberId,partyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/parties/{party-id}/like-status")
    public ResponseEntity<Boolean> checkBookmarkStatus(@PathVariable("party-id")@Positive long partyId,
                                                       @RequestParam @Positive long memberId){
        boolean isBookmarked = bookmarkService.isPartyBookmarkedByUser(memberId,partyId);
        return ResponseEntity.ok(isBookmarked);
    }
}
