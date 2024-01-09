package com.ssdam.member.mapper;

import com.ssdam.member.dto.MemberPatchDto;
import com.ssdam.member.dto.MemberPostDto;
import com.ssdam.member.dto.MemberResponseDto;
import com.ssdam.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member memberPostDtoToMember(MemberPostDto memberPostDto);

    Member memberPatchDtoToMember(MemberPatchDto memberPatchDto);

    MemberResponseDto memberToMemberResponseDto(Member member);

    List<MemberResponseDto> membersToMemberResponseDto(List<Member> members);
}
