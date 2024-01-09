package com.ssdam.member.dto;

import com.ssdam.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDto {
    private long memberId;
    private String email;
    private String nickname;
    private Member.MemberStatus memberStatus;

    public String getMemberStatus() {
        return memberStatus.getStatus();
    }
}
