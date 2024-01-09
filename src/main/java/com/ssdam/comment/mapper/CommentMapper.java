package com.ssdam.comment.mapper;

import com.ssdam.comment.dto.CommentDto;
import com.ssdam.comment.entity.Comment;
import com.ssdam.member.entity.Member;
import com.ssdam.party.entity.Party;
import org.hibernate.annotations.Target;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    default Comment commentPostDtoToComment(CommentDto.Post requestBody){
        Member member = new Member();
        Party party = new Party();
        Comment comment = new Comment();

        member.setMemberId(requestBody.getMemberId());
        party.setPartyId(requestBody.getPartyId());
        comment.setMember(member);
        comment.setParty(party);
        comment.setComment(requestBody.getComment());
        return comment;
    }
    Comment commentPatchDtoToComment(CommentDto.Patch requestBody);
    @Mapping(target = "partyId", source = "comment.party.partyId")
    @Mapping(target = "nickname", source = "comment.member.nickname")
    CommentDto.Response commentToCommentResponse(Comment comment);
    List<CommentDto.Response> commentsToCommentResponses(List<Comment> comments);
}
