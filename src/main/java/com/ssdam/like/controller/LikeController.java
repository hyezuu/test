package com.ssdam.like.controller;

import com.ssdam.like.service.LikeService;
import com.ssdam.member.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }
    public ResponseEntity addLike(@PathVariable("commentId")@Positive long commentId,
                                  @PathVariable("memberId")@Positive long memberId){
        Member member = new Member();//member Repository or memberService에서 member가져오는 로직 필요

        likeService.addLike(commentId,member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
