package com.ssdam.comment.controller;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.ssdam.comment.dto.CommentDto;
import com.ssdam.comment.entity.Comment;
import com.ssdam.comment.mapper.CommentMapper;
import com.ssdam.comment.service.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @MockBean
    private CommentMapper mapper;
    @Autowired
    private Gson gson;

    @Test
    public void postCommentTest() throws Exception {
        //given
        CommentDto.Post post = (CommentDto.Post) CommentStub.getRequestBody(HttpMethod.POST);
        String content = gson.toJson(post);

        given(mapper.commentPostDtoToComment(Mockito.any(CommentDto.Post.class))).willReturn(new Comment());
        Comment mockResultComment = new Comment();
        mockResultComment.setCommentId(1L);
        given(commentService.createComment(Mockito.any(Comment.class))).willReturn(mockResultComment);
        //when
        ResultActions actions =
                mockMvc.perform(
                        post("/v1/comments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        //then
        ConstraintDescriptions postCommentConstraints = new ConstraintDescriptions(CommentDto.Post.class);
        List<String> commentDescriptions = postCommentConstraints.descriptionsForProperty("comment");

        actions
                .andExpect(status().isCreated())
                .andExpect(header().string("location", is(startsWith("/v1/comments/"))))
                .andDo(document("post-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                List.of(
                                        fieldWithPath("partyId").type(JsonFieldType.NUMBER).description("모임 식별자"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용")
                                                .attributes(key("constraints").value(commentDescriptions))
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header. 등록된 리소스의 URI. ex)v1/comments/1")
                        )
                ));
    }


    @Test
    public void patchCommentTest() throws Exception {
        //given
        long commentId = 1L;

        CommentDto.Patch patch = (CommentDto.Patch) CommentStub.getRequestBody(HttpMethod.PATCH);
        String content = gson.toJson(patch);

        CommentDto.Response responseDto = CommentStub.getSingleResponseBody();

        given(mapper.commentPatchDtoToComment(Mockito.any(CommentDto.Patch.class))).willReturn(new Comment());
        given(commentService.updateComment(Mockito.any(Comment.class))).willReturn(new Comment());
        given(mapper.commentToCommentResponse(Mockito.any(Comment.class))).willReturn(responseDto);
        //when
        ResultActions actions =
                mockMvc.perform(
                        patch("/v1/comments/{comment-id}", commentId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        //then
        ConstraintDescriptions patchCommentConstraints = new ConstraintDescriptions(CommentDto.Patch.class);
        List<String> commentDescriptions = patchCommentConstraints.descriptionsForProperty("comment");

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentId").value(commentId))
                .andExpect(jsonPath("$.data.partyTitle").value(responseDto.getPartyTitle()))
                .andExpect(jsonPath("$.data.nickname").value(responseDto.getNickname()))
                .andExpect(jsonPath("$.data.likeCount").value(responseDto.getLikeCount()))
                .andExpect(jsonPath("$.data.comment").value(responseDto.getComment()))
                .andExpect(jsonPath("$.data.reply.replyId").value(responseDto.getReply().getReplyId()))
                .andExpect(jsonPath("$.data.reply.reply").value(responseDto.getReply().getReply()))
                .andExpect(jsonPath("$.data.reply.nickname").value(responseDto.getReply().getNickname()))
                .andExpect(jsonPath("$.data.reply.createdAt").value(responseDto.getReply().getCreatedAt().toString()))
                .andExpect(jsonPath("$.data.reply.modifiedAt").value(responseDto.getReply().getModifiedAt().toString()))
                .andExpect(jsonPath("$.data.createdAt").value(responseDto.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data.modifiedAt").value(responseDto.getModifiedAt().toString()))
                .andDo(document("patch-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                List.of(parameterWithName("comment-id").description("댓글 식별자 ID"))
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 식별자")
                                                .ignored(),
                                        fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용")
                                                .attributes(key("constraints").value(commentDescriptions))
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                        fieldWithPath("data.partyTitle").type(JsonFieldType.STRING).description("모집글 제목"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                        fieldWithPath("data.comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("data.reply").type(JsonFieldType.OBJECT).description("대댓글"),
                                        fieldWithPath("data.reply.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                        fieldWithPath("data.reply.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                        fieldWithPath("data.reply.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                        fieldWithPath("data.reply.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                        fieldWithPath("data.reply.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("댓글 작성 날짜"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("댓글 수정 날짜")
                                )
                        )
                ));
    }

    @Test
    public void getCommentTest() throws Exception {
        //given
        long commentId = 1L;
        CommentDto.Response responseDto = CommentStub.getSingleResponseBody();

        given(commentService.findComment(Mockito.anyLong())).willReturn(new Comment());
        given(mapper.commentToCommentResponse(Mockito.any(Comment.class))).willReturn(responseDto);
        //when
        ResultActions actions =
                mockMvc.perform(
                        get("/v1/comments/{comment-id}", commentId)
                                .accept(MediaType.APPLICATION_JSON)
                );
        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentId").value(commentId))
                .andExpect(jsonPath("$.data.partyTitle").value(responseDto.getPartyTitle()))
                .andExpect(jsonPath("$.data.nickname").value(responseDto.getNickname()))
                .andExpect(jsonPath("$.data.likeCount").value(responseDto.getLikeCount()))
                .andExpect(jsonPath("$.data.comment").value(responseDto.getComment()))
                .andExpect(jsonPath("$.data.reply.replyId").value(responseDto.getReply().getReplyId()))
                .andExpect(jsonPath("$.data.reply.reply").value(responseDto.getReply().getReply()))
                .andExpect(jsonPath("$.data.reply.nickname").value(responseDto.getReply().getNickname()))
                .andExpect(jsonPath("$.data.reply.createdAt").value(responseDto.getReply().getCreatedAt().toString()))
                .andExpect(jsonPath("$.data.reply.modifiedAt").value(responseDto.getReply().getModifiedAt().toString()))
                .andExpect(jsonPath("$.data.createdAt").value(responseDto.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data.modifiedAt").value(responseDto.getModifiedAt().toString()))
                .andDo(document("get-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                List.of(parameterWithName("comment-id").description("댓글 식별자 ID"))
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터").optional(),
                                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                        fieldWithPath("data.partyTitle").type(JsonFieldType.STRING).description("모집글 제목"),
                                        fieldWithPath("data.reply").type(JsonFieldType.OBJECT).description("대댓글"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                        fieldWithPath("data.comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("data.reply").type(JsonFieldType.OBJECT).description("대댓글"),
                                        fieldWithPath("data.reply.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                        fieldWithPath("data.reply.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                        fieldWithPath("data.reply.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                        fieldWithPath("data.reply.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                        fieldWithPath("data.reply.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("댓글 작성 날짜"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("댓글 수정 날짜")
                                )
                        )
                ));
    }

    @Test
    public void getCommentsTest() throws Exception {
        //given
        String page = "1";
        String size = "10";

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", page);
        queryParams.add("size", size);

        Page<Comment> comments = CommentStub.getMultiResultComments();
        List<CommentDto.Response> responses = CommentStub.getMultiResponseBody();

        given(commentService.findComments(Mockito.anyInt(), Mockito.anyInt())).willReturn(comments);
        given(mapper.commentsToCommentResponses(Mockito.anyList())).willReturn(responses);
        //when
        ResultActions actions = mockMvc.perform(
                get("/v1/comments")
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult result =
                actions.andExpect(status().isOk())
                        .andDo(
                                document(
                                        "get-comments",
                                        preprocessRequest(prettyPrint()),
                                        preprocessResponse(prettyPrint()),
                                        requestParameters(
                                                List.of(
                                                        parameterWithName("page").description("Page 번호"),
                                                        parameterWithName("size").description("Page Size")
                                                )
                                        ),
                                        responseFields(
                                                List.of(
                                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터").optional(),
                                                        fieldWithPath("data[].commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                                        fieldWithPath("data[].partyTitle").type(JsonFieldType.STRING).description("모집글 제목"),
                                                        fieldWithPath("data[].nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                        fieldWithPath("data[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                                        fieldWithPath("data[].reply").type(JsonFieldType.OBJECT).description("대댓글"),
                                                        fieldWithPath("data[].reply.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                                        fieldWithPath("data[].reply.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                                        fieldWithPath("data[].reply.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                                        fieldWithPath("data[].reply.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                                        fieldWithPath("data[].reply.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜"),
                                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("댓글 작성 날짜"),
                                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("댓글 수정 날짜"),
                                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 건 수"),
                                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                                )
                                        )

                                )
                        )
                        .andReturn();
        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");
        assertThat(list.size(), is(3));
    }

    @Test
    public void getCommentsByMemberTest() throws Exception {
        //given
        String page = "1";
        String size = "10";
        String memberId = "1";

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", page);
        queryParams.add("size", size);
        queryParams.add("memberId", memberId);

        Page<Comment> comments = CommentStub.getMultiResultCommentsByMember();
        List<CommentDto.Response> responses = CommentStub.getMultiResponseBodyByMember();

        given(commentService.findCommentsByMember(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(comments);
        given(mapper.commentsToCommentResponses(Mockito.anyList())).willReturn(responses);
        //when
        ResultActions actions = mockMvc.perform(
                get("/v1/comments")
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult result =
                actions.andExpect(status().isOk())
                        .andDo(
                                document(
                                        "get-comments-by-member",
                                        preprocessRequest(prettyPrint()),
                                        preprocessResponse(prettyPrint()),
                                        requestParameters(
                                                List.of(
                                                        parameterWithName("page").description("Page 번호"),
                                                        parameterWithName("size").description("Page Size"),
                                                        parameterWithName("memberId").description("회원 식별자 ID")
                                                )
                                        ),
                                        responseFields(
                                                List.of(
                                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터").optional(),
                                                        fieldWithPath("data[].commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                                        fieldWithPath("data[].partyTitle").type(JsonFieldType.STRING).description("모집글 제목"),
                                                        fieldWithPath("data[].nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                        fieldWithPath("data[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                                        fieldWithPath("data[].reply").type(JsonFieldType.OBJECT).description("대댓글"),
                                                        fieldWithPath("data[].reply.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                                        fieldWithPath("data[].reply.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                                        fieldWithPath("data[].reply.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                                        fieldWithPath("data[].reply.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                                        fieldWithPath("data[].reply.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜"),
                                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("댓글 작성 날짜"),
                                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("댓글 수정 날짜"),
                                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 건 수"),
                                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                                )
                                        )

                                )
                        )
                        .andReturn();
        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");
        assertThat(list.size(), is(2));
    }

    @Test
    public void getCommentsByPartyTest() throws Exception {
        //given
        String page = "1";
        String size = "10";
        String partyId = "1";

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", page);
        queryParams.add("size", size);
        queryParams.add("partyId", partyId);

        Page<Comment> comments = CommentStub.getMultiResultCommentsByParty();
        List<CommentDto.Response> responses = CommentStub.getMultiResponseBodyByParty();

        given(commentService.findCommentsByParty(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(comments);
        given(mapper.commentsToCommentResponses(Mockito.anyList())).willReturn(responses);
        //when
        ResultActions actions = mockMvc.perform(
                get("/v1/comments")
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult result =
                actions.andExpect(status().isOk())
                        .andDo(
                                document(
                                        "get-comments-by-party",
                                        preprocessRequest(prettyPrint()),
                                        preprocessResponse(prettyPrint()),
                                        requestParameters(
                                                List.of(
                                                        parameterWithName("page").description("Page 번호"),
                                                        parameterWithName("size").description("Page Size"),
                                                        parameterWithName("partyId").description("모임 식별자 ID")
                                                )
                                        ),
                                        responseFields(
                                                List.of(
                                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터").optional(),
                                                        fieldWithPath("data[].commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                                        fieldWithPath("data[].partyTitle").type(JsonFieldType.STRING).description("모집글 제목"),
                                                        fieldWithPath("data[].nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                        fieldWithPath("data[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                                        fieldWithPath("data[].reply").type(JsonFieldType.OBJECT).description("대댓글"),
                                                        fieldWithPath("data[].reply.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                                        fieldWithPath("data[].reply.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                                        fieldWithPath("data[].reply.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                                        fieldWithPath("data[].reply.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                                        fieldWithPath("data[].reply.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜"),
                                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("댓글 작성 날짜"),
                                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("댓글 수정 날짜"),
                                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 건 수"),
                                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                                )
                                        )

                                )
                        )
                        .andReturn();
        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");
        assertThat(list.size(), is(2));
    }
    @Test
    public void getCommentsByPartyByLikeCountTest() throws Exception {
        //given
        String page = "1";
        String size = "10";
        String partyId = "1";

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", page);
        queryParams.add("size", size);
        queryParams.add("partyId", partyId);

        Page<Comment> comments = CommentStub.getMultiResultCommentsByParty();
        List<CommentDto.Response> responses = CommentStub.getMultiResponseBodyByPartyByLikeCount();

        given(commentService.findCommentsByPartySortByLikes(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(comments);
        given(mapper.commentsToCommentResponses(Mockito.anyList())).willReturn(responses);
        //when
        ResultActions actions = mockMvc.perform(
                get("/v1/comments/likes")
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult result =
                actions.andExpect(status().isOk())
                        .andDo(
                                document(
                                        "get-comments-by-party-by-likeCount",
                                        preprocessRequest(prettyPrint()),
                                        preprocessResponse(prettyPrint()),
                                        requestParameters(
                                                List.of(
                                                        parameterWithName("page").description("Page 번호"),
                                                        parameterWithName("size").description("Page Size"),
                                                        parameterWithName("partyId").description("모임 식별자 ID")
                                                )
                                        ),
                                        responseFields(
                                                List.of(
                                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터").optional(),
                                                        fieldWithPath("data[].commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                                        fieldWithPath("data[].partyTitle").type(JsonFieldType.STRING).description("모집글 제목"),
                                                        fieldWithPath("data[].nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                        fieldWithPath("data[].comment").type(JsonFieldType.STRING).description("댓글 내용"),
                                                        fieldWithPath("data[].reply").type(JsonFieldType.OBJECT).description("대댓글"),
                                                        fieldWithPath("data[].reply.replyId").type(JsonFieldType.NUMBER).description("대댓글 식별자"),
                                                        fieldWithPath("data[].reply.reply").type(JsonFieldType.STRING).description("대댓글 내용"),
                                                        fieldWithPath("data[].reply.nickname").type(JsonFieldType.STRING).description("대댓글 작성자"),
                                                        fieldWithPath("data[].reply.createdAt").type(JsonFieldType.STRING).description("대댓글 작성 날짜"),
                                                        fieldWithPath("data[].reply.modifiedAt").type(JsonFieldType.STRING).description("대댓글 수정 날짜"),
                                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("댓글 작성 날짜"),
                                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("댓글 수정 날짜"),
                                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 건 수"),
                                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                                )
                                        )

                                )
                        )
                        .andReturn();
        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");
        assertThat(list.size(), is(2));
    }

    @Test
    public void deleteCommentTest() throws Exception {
        //given
        long commentId = 1L;

        doNothing().when(commentService).deleteComment(Mockito.anyLong());
        //when
        ResultActions actions = mockMvc.perform(
                delete("/v1/comments/{comment-id}", commentId));
        //then
        actions
                .andExpect(status().isNoContent())
                .andDo(document("delete-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("comment-id").description("댓글 식별자")
                        )));
    }
}
