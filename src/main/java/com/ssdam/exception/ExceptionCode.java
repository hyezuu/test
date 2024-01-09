package com.ssdam.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    PARTY_NOT_FOUND(404, "Party not found"),
    COMMENT_NOT_FOUND(404, "Comment not found"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    INVALID_MEMBER_STATUS(400, "Invalid member status"),
    TODOLIST_NOT_FOUND(404, "TodoList not found"),
    SERVER_UNAVAILABLE(503,"Service Unavailable");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
