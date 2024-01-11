package com.ssdam.comment.mapper;

import com.ssdam.comment.dto.CommentDto;
import com.ssdam.comment.entity.Comment;
import com.ssdam.member.entity.Member;
import com.ssdam.party.entity.Party;
import com.ssdam.reply.dto.ReplyDto;
import com.ssdam.reply.mapper.ReplyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    ReplyMapper replyMapper = ReplyMapper.create();


    default Comment commentPostDtoToComment(CommentDto.Post requestBody) {
        Member member = new Member();
        Party party = new Party();
        Comment comment = new Comment();

        member.setMemberId(requestBody.getMemberId());
        party.setPartyId(requestBody.getPartyId());
        comment.addMember(member);
        comment.addParty(party);
        comment.setComment(requestBody.getComment());
        return comment;
    }

    Comment commentPatchDtoToComment(CommentDto.Patch requestBody);

    default CommentDto.Response commentToCommentResponse(Comment comment) {
        ReplyDto.Response reply = replyMapper.replyToReplyResponse(comment.getReply());


        return CommentDto.Response.builder()
                .commentId(comment.getCommentId())
                .partyTitle(comment.getParty().getTitle())
                .nickname(comment.getMember().getNickname())
                .likeCount(comment.getLikeCount())
                .comment(comment.getComment())
                .reply(reply)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    List<CommentDto.Response> commentsToCommentResponses(List<Comment> comments);
}
