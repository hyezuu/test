package com.ssdam.comment.controller;

import com.ssdam.comment.dto.CommentDto;
import com.ssdam.comment.entity.Comment;
import com.ssdam.member.entity.Member;
import com.ssdam.party.entity.Party;
import com.ssdam.reply.controller.ReplyStub;
import com.ssdam.reply.dto.ReplyDto;
import com.ssdam.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentStub {
    private static Map<HttpMethod, Object> stubRequestBody;
    static {
        stubRequestBody = new HashMap<>();
        stubRequestBody.put(HttpMethod.POST,
                new CommentDto.Post(1L,1L, "Sample Comment"));
        stubRequestBody.put(HttpMethod.PATCH,
                CommentDto.Patch.builder()
                        .commentId(1L).comment("Comment").build());
    }
    public static Object getRequestBody(HttpMethod method) {
        return stubRequestBody.get(method);
    }

    public static CommentDto.Response getSingleResponseBody() {
        return CommentDto.Response.builder()
                .commentId(1L)
                .partyTitle("모임")
                .nickname("쓰담")
                .likeCount(0)
                .reply(getReplyDtoResponse())
                .comment("Comment")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static List<CommentDto.Response> getMultiResponseBody() {

        CommentDto.Response responseDto1
                = CommentDto.Response.builder()
                .commentId(1L)
                .partyTitle("모임1")
                .nickname("쓰담1")
                .likeCount(1)
                .reply(getReplyDtoResponse())
                .comment("Comment1")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        CommentDto.Response responseDto2
                = CommentDto.Response.builder()
                .commentId(2L)
                .partyTitle("모임2")
                .nickname("쓰담2")
                .likeCount(2)
                .reply(getReplyDtoResponse())
                .comment("Comment2")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        CommentDto.Response responseDto3
                = CommentDto.Response.builder()
                .commentId(3L)
                .partyTitle("모임3")
                .nickname("쓰담3")
                .likeCount(3)
                .reply(getReplyDtoResponse())
                .comment("Comment3")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        return List.of(responseDto1,responseDto2,responseDto3);
    }
    public static List<CommentDto.Response> getMultiResponseBodyByParty() {
        CommentDto.Response responseDto1
                = CommentDto.Response.builder()
                .commentId(1L)
                .partyTitle("모임1")
                .nickname("쓰담1")
                .likeCount(1)
                .reply(getReplyDtoResponse())
                .comment("Comment1")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        CommentDto.Response responseDto2
                = CommentDto.Response.builder()
                .commentId(2L)
                .partyTitle("모임1")
                .nickname("쓰담2")
                .likeCount(2)
                .reply(getReplyDtoResponse())
                .comment("Comment2")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        return List.of(responseDto1,responseDto2);
    }
    public static List<CommentDto.Response> getMultiResponseBodyByPartyByLikeCount() {
        CommentDto.Response responseDto1
                = CommentDto.Response.builder()
                .commentId(1L)
                .partyTitle("모임1")
                .nickname("쓰담1")
                .likeCount(1)
                .reply(getReplyDtoResponse())
                .comment("Comment1")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        CommentDto.Response responseDto2
                = CommentDto.Response.builder()
                .commentId(2L)
                .partyTitle("모임1")
                .nickname("쓰담2")
                .likeCount(2)
                .reply(getReplyDtoResponse())
                .comment("Comment2")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        return List.of(responseDto2,responseDto1);
    }
    public static List<CommentDto.Response> getMultiResponseBodyByMember() {
        CommentDto.Response responseDto1
                = CommentDto.Response.builder()
                .commentId(1L)
                .partyTitle("모임1")
                .nickname("쓰담1")
                .likeCount(1)
                .reply(getReplyDtoResponse())
                .comment("Comment1")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        CommentDto.Response responseDto3
                = CommentDto.Response.builder()
                .commentId(3L)
                .partyTitle("모임2")
                .nickname("쓰담1")
                .likeCount(3)
                .reply(getReplyDtoResponse())
                .comment("Comment3")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        return List.of(responseDto1,responseDto3);
    }

    public static Comment getSingleResultComment() {
        Party party1 = new Party();
        party1.setPartyId(1L);
        Member member1 = new Member();
        member1.setMemberId(1L);
        Reply reply = new Reply();

        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setMember(member1);
        comment.setParty(party1);
        comment.setComment("Comment");
        comment.setReply(reply);

        return comment;
    }

    public static Page<Comment> getMultiResultComments() {
        Party party1 = new Party();
        party1.setPartyId(1L);

        Party party2 = new Party();
        party2.setPartyId(2L);

        Member member1 = new Member();
        member1.setMemberId(1L);

        Member member2 = new Member();
        member2.setMemberId(2L);

        Reply reply = new Reply();

        Comment comment1 = new Comment();
        comment1.setCommentId(1L);
        comment1.setMember(member1);
        comment1.setParty(party1);
        comment1.setReply(reply);
        comment1.setComment("Comment1");

        Comment comment2 = new Comment();
        comment2.setCommentId(2L);
        comment2.setMember(member2);
        comment2.setParty(party1);
        comment2.setReply(reply);
        comment2.setComment("Comment2");

        Comment comment3 = new Comment();
        comment3.setCommentId(3L);
        comment3.setMember(member1);
        comment3.setParty(party2);
        comment3.setReply(reply);
        comment3.setComment("Comment3");

        return new PageImpl<>(List.of(comment1,comment2,comment3),
                PageRequest.of(0, 10, Sort.by("createdAt").descending()),3);
    }
    public static Page<Comment> getMultiResultCommentsByMember() {
        Party party1 = new Party();
        party1.setPartyId(1L);

        Party party2 = new Party();
        party2.setPartyId(2L);

        Member member1 = new Member();
        member1.setMemberId(1L);

        Reply reply = new Reply();

        Comment comment1 = new Comment();
        comment1.setCommentId(1L);
        comment1.setMember(member1);
        comment1.setParty(party1);
        comment1.setReply(reply);
        comment1.setComment("Comment1");

        Comment comment3 = new Comment();
        comment3.setCommentId(3L);
        comment3.setMember(member1);
        comment3.setParty(party2);
        comment3.setReply(reply);
        comment3.setComment("Comment3");

        return new PageImpl<>(List.of(comment1,comment3),
                PageRequest.of(0, 10, Sort.by("createdAt").descending()),2);
    }
    public static Page<Comment> getMultiResultCommentsByParty() {
        Party party1 = new Party();
        party1.setPartyId(1L);

        Member member1 = new Member();
        member1.setMemberId(1L);

        Member member2 = new Member();
        member2.setMemberId(2L);

        Reply reply = new Reply();

        Comment comment1 = new Comment();
        comment1.setCommentId(1L);
        comment1.setMember(member1);
        comment1.setParty(party1);
        comment1.setReply(reply);
        comment1.setComment("Comment1");

        Comment comment2 = new Comment();
        comment2.setCommentId(2L);
        comment2.setMember(member2);
        comment2.setParty(party1);
        comment2.setReply(reply);
        comment2.setComment("Comment2");

        return new PageImpl<>(List.of(comment1,comment2),
                PageRequest.of(0, 10, Sort.by("createdAt").descending()),2);
    }
    public static ReplyDto.Response getReplyDtoResponse(){
       return ReplyStub.getSingleResponseBody();
    }

}
