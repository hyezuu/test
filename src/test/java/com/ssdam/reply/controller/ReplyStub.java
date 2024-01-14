package com.ssdam.reply.controller;

import com.ssdam.reply.dto.ReplyDto;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ReplyStub {
    private static Map<HttpMethod, Object> stubRequestBody;
    static {
        stubRequestBody = new HashMap<>();
        stubRequestBody.put(HttpMethod.POST,
                new ReplyDto.Post(1L, 1L, "Sample Reply"));
        stubRequestBody.put(HttpMethod.PATCH,
                new ReplyDto.Patch(1L,"Reply"));
    }
    public static Object getRequestBody(HttpMethod method){
        return stubRequestBody.get(method);
    }

    public static ReplyDto.Response getSingleResponseBody() {
        return ReplyDto.Response.builder()
                .replyId(1L)
                .reply("Reply")
                .nickname("쓰담쓰담")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
