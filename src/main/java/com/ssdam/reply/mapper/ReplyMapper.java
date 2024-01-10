package com.ssdam.reply.mapper;

import com.ssdam.comment.entity.Comment;
import com.ssdam.member.entity.Member;
import com.ssdam.reply.dto.ReplyDto;
import com.ssdam.reply.entity.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReplyMapper {
    default Reply replyPostDtoToReply(ReplyDto.Post requestBody){
        Member member = new Member();
        Comment comment = new Comment();
        Reply reply = new Reply();

        member.setMemberId(requestBody.getMemberId());
        comment.setCommentId(requestBody.getMemberId());
        reply.addMember(member);
        reply.addComment(comment);
        reply.setReply(requestBody.getReply());
        return reply;
    }

    Reply replyPatchDtoToReply(ReplyDto.Patch requestBody);

    @Mapping(target = "nickname", source = "reply.member.nickname")
    ReplyDto.Response replyToReplyResponse(Reply reply);
}
