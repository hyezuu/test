package com.ssdam.reply.dto;

import com.ssdam.validator.NotSpace;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ReplyDto {
    @Getter
    public static class Post{
        private long commentId;
        private long memberId;
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String reply;
    }
    @Getter
    @Setter
    public static class Patch{
        private long replyId;
        @NotSpace(message = "내용은 필수 입력 사항입니다.")
        private String reply;
    }

    @Getter
    @Builder
    public static class Response{
        private long replyId;
        private String reply;
        private String nickname;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

}
