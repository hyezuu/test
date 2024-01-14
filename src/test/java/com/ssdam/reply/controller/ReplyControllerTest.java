package com.ssdam.reply.controller;

import com.google.gson.Gson;
import com.ssdam.reply.dto.ReplyDto;
import com.ssdam.reply.entity.Reply;
import com.ssdam.reply.mapper.ReplyMapper;
import com.ssdam.reply.service.ReplyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReplyController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class ReplyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReplyService replyService;
    @MockBean
    private ReplyMapper mapper;
    @Autowired
    private Gson gson;

    @Test
    public void postReplyTest() throws Exception {
        //given
        ReplyDto.Post post = (ReplyDto.Post) ReplyStub.getRequestBody(HttpMethod.POST);
        String content = gson.toJson(post);

        given(mapper.replyPostDtoToReply(Mockito.any(ReplyDto.Post.class))).willReturn(new Reply());
        Reply mockResultReply = new Reply();
        mockResultReply.setReplyId(1L);
        given(replyService.createReply(Mockito.any(Reply.class))).willReturn(mockResultReply);
        //when
        ResultActions actions =
                mockMvc.perform(
                        post("/v1/replies")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        //then
        ConstraintDescriptions postReplyConstraints = new ConstraintDescriptions(ReplyDto.Post.class);
        List<String> replyDescriptions = postReplyConstraints.descriptionsForProperty("reply");

        actions
                .andExpect(status().isCreated())
                .andExpect(header().string("location", is(startsWith("/v1/replies/"))))
                .andDo(document("post-reply",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                List.of(
                                        fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("reply").type(JsonFieldType.STRING).description("대댓글 내용")
                                                .attributes(key("constraints").value(replyDescriptions))
                                                .optional()
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header. 등록된 리소스의 URI. ex)v1/replies/1")
                        )
                ));
    }
    @Test
    public void patchReplyTest() throws Exception {
        //given
        long replyId = 1L;

        ReplyDto.Patch patch = (ReplyDto.Patch) ReplyStub.getRequestBody(HttpMethod.PATCH);
        String content = gson.toJson(patch);

        ReplyDto.Response responseDto = ReplyStub.getSingleResponseBody();

        given(mapper.replyPatchDtoToReply(Mockito.any(ReplyDto.Patch.class))).willReturn(new Reply());
        given(replyService.updateReply(Mockito.any(Reply.class))).willReturn(new Reply());
        given(mapper.replyToReplyResponse(Mockito.any(Reply.class))).willReturn(responseDto);
        //when
        ResultActions actions =
                mockMvc.perform(
                        patch("/v1/replies/{reply-id}", replyId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.replyId").value(replyId))
                .andExpect(jsonPath("$.data.reply").value(responseDto.getReply()))
                .andExpect(jsonPath("$.data.nickname").value(responseDto.getNickname()))
                .andExpect(jsonPath("$.data.createdAt").value(responseDto.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data.modifiedAt").value(responseDto.getModifiedAt().toString()))
                .andDo(document("patch-reply",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                List.of(parameterWithName("reply-id").description("대댓글 식별자 ID"))
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자")
                                                .ignored(),
                                        fieldWithPath("reply").type(JsonFieldType.STRING).description("대댓글 내용")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과데이터"),
                                        fieldWithPath("data.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                        fieldWithPath("data.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜")
                                )
                        )

                ));
    }
    @Test
    public void getReplyTest() throws Exception {
        //given
        long replyId = 1L;
        ReplyDto.Response responseDto = ReplyStub.getSingleResponseBody();

        given(replyService.findReply(Mockito.anyLong())).willReturn(new Reply());
        given(mapper.replyToReplyResponse(Mockito.any(Reply.class))).willReturn(responseDto);
        //when
        ResultActions actions =
                mockMvc.perform(
                        get("/v1/replies/{reply-id}", replyId)
                                .accept(MediaType.APPLICATION_JSON)
                );
        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.replyId").value(replyId))
                .andExpect(jsonPath("$.data.reply").value(responseDto.getReply()))
                .andExpect(jsonPath("$.data.nickname").value(responseDto.getNickname()))
                .andExpect(jsonPath("$.data.createdAt").value(responseDto.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data.modifiedAt").value(responseDto.getModifiedAt().toString()))
                .andDo(document("get-reply",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                List.of(parameterWithName("reply-id").description("대댓글 식별자 ID"))
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과데이터"),
                                        fieldWithPath("data.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                        fieldWithPath("data.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜")
                                )
                        )

                ));
    }

    @Test
    public void deleteReplyTest() throws Exception {
        //given
        long replyId = 1L;

        doNothing().when(replyService).deleteReply(Mockito.anyLong());
        //when
        ResultActions actions = mockMvc.perform(
                delete("/v1/replies/{reply-id}",replyId));
        //then
        actions
                .andExpect(status().isNoContent())
                .andDo(document("delete-reply",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reply-id").description("대댓글 식별자")
                        )));
    }
}