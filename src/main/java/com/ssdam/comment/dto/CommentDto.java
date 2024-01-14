package com.ssdam.comment.dto;

import com.ssdam.reply.dto.ReplyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public class CommentDto {
    @Getter
    @AllArgsConstructor
    public static class Post {

        private long partyId;
        @Positive
        private long memberId;
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String comment;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Setter
    public static class Patch {

        private long commentId;
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String comment;

    }

    @Builder
    @Getter
    public static class Response {
        private long commentId;
        private String partyTitle;
        private String nickname;
        private int likeCount;
        private String comment;
        private ReplyDto.Response reply;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

}
