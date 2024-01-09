package com.ssdam.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TodoListDto {
    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String title;
        @NotNull
        private Integer todoOrder;
    }

    @Getter
    public static class Patch {
        private long todolistId;
        private String title;
        private Integer todoOrder;
        public void addTodoListId(long todolistId) {
            this.todolistId = todolistId;
        }
    }

    @Getter
    @Builder
    public static class Response {
        private long todolistId;
        private String title;
        private Integer todoOrder;
        private String createdAt;
        private String modifiedAt;
    }
}
