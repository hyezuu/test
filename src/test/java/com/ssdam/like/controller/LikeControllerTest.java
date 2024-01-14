package com.ssdam.like.controller;

import com.ssdam.like.service.LikeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LikeService likeService;

    @Test
    public void toggleLikeToCommentTest() throws Exception {
        //given
        long commentId = 1L;
        long memberId = 1L;

        doNothing().when(likeService).toggleLike(Mockito.anyLong(), Mockito.anyLong());
        //when
        ResultActions actions = mockMvc.perform(
                post("/v1/likes/comments/{comment-id}", commentId)
                        .param("memberId", String.valueOf(memberId)));
        //then
        actions
                .andExpect(status().isOk())
                .andDo(document("toggle-like-to-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("comment-id").description("댓글 식별자")
                        ),
                        requestParameters(
                                parameterWithName("memberId").description("회원 식별자 ID")
                        )));
    }

    @Test
    public void checkLikeStatusTest() throws Exception {
        //given
        long commentId = 1L;
        long memberId = 1L;
        boolean isLiked = true;

        given(likeService.isCommentLikedByUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(isLiked);
        //when
        ResultActions actions = mockMvc.perform(
                get("/v1/likes/comments/{comment-id}/like-status", commentId)
                        .param("memberId", String.valueOf(memberId))
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLiked").value(isLiked))
                .andDo(document("check-like-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("comment-id").description("댓글 식별자")
                        ),
                        requestParameters(
                                parameterWithName("memberId").description("회원 식별자 ID")
                        ),
                        responseFields(
                                fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"))
                ));
    }

}