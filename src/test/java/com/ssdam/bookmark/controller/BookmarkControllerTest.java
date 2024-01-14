package com.ssdam.bookmark.controller;

import com.ssdam.bookmark.service.BookmarkService;
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

@WebMvcTest(BookmarkController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class BookmarkControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookmarkService bookmarkService;

    @Test
    public void toggleBookmarkToPartyTest() throws Exception {
        //given
        long partyId = 1L;
        long memberId = 1L;

        doNothing().when(bookmarkService).toggleBookmark(Mockito.anyLong(), Mockito.anyLong());
        //when
        ResultActions actions = mockMvc.perform(
                post("/v1/bookmarks/parties/{party-id}", partyId)
                        .param("memberId", String.valueOf(memberId)));
        //then
        actions
                .andExpect(status().isOk())
                .andDo(document("toggle-bookmark-to-party",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("party-id").description("모집글 식별자")
                        ),
                        requestParameters(
                                parameterWithName("memberId").description("회원 식별자 ID")
                        )));
    }

    @Test
    public void checkBookmarkStatusTest() throws Exception {
        //given
        long partyId = 1L;
        long memberId = 1L;
        boolean isBookmarked = true;

        given(bookmarkService.isPartyBookmarkedByUser(Mockito.anyLong(), Mockito.anyLong())).willReturn(isBookmarked);
        //when
        ResultActions actions = mockMvc.perform(
                get("/v1/bookmarks/parties/{party-id}/bookmark-status", partyId)
                        .param("memberId", String.valueOf(memberId))
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isBookmarked").value(isBookmarked))
                .andDo(document("check-bookmark-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("party-id").description("모집글 식별자")
                        ),
                        requestParameters(
                                parameterWithName("memberId").description("회원 식별자 ID")
                        ),
                        responseFields(
                                fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"))
                ));
    }

}