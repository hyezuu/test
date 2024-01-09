package com.ssdam.member.dto;

import com.ssdam.member.entity.Member;
import com.ssdam.validator.NotSpace;
import lombok.Getter;

@Getter
public class MemberPatchDto {
    private long memberId;

    @NotSpace(message = "닉네임은 공백이 아니어야 합니다.")
    private String nickname;

    private Member.MemberStatus memberStatus;

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
