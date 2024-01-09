package com.ssdam.comment.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class CommentDto {
    @Getter
    public static class Post {

        private long partyId;
        @Positive
        private long memberId;
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String comment;

        public Post(long memberId, String comment) {
            this.memberId = memberId;
            this.comment = comment;
        }
    }

    @Getter
    @Builder
    public static class Patch {

        private long commentId;
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String comment;

        public void setCommentId(long commentId) {
            this.commentId = commentId;
        }
    }

    @Builder
    @Getter
    public static class Response {
        private long commentId;
        private long partyId;
        private String nickname;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

}
