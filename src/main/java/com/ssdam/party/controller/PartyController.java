package com.ssdam.party.controller;

import com.ssdam.dto.MultiResponseDto;
import com.ssdam.dto.SingleResponseDto;
import com.ssdam.member.entity.Member;
import com.ssdam.member.service.MemberService;
import com.ssdam.party.dto.PartyDto;
import com.ssdam.party.entity.Party;
import com.ssdam.party.mapper.PartyMapper;
import com.ssdam.party.service.PartyService;
import com.ssdam.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
public class PartyController {
    private final static String PARTY_DEFAULT_URL = "/v1/parties";
    private final PartyService partyService;
    private final MemberService memberService;
    private final PartyMapper mapper;

    public PartyController(PartyService partyService, MemberService memberService, PartyMapper mapper) {
        this.partyService = partyService;
        this.memberService = memberService;
        this.mapper = mapper;
    }

    // 파티 생성
    @RequestMapping(value = "/v1/parties", method = RequestMethod.POST)
    public ResponseEntity postParty(@RequestBody @Valid PartyDto.Post requestBody) {
        Member member = memberService.findMember(requestBody.getMemberId());
        Party party = mapper.partyPostDtoToParty(requestBody);
        party.setMember(member);
        Party createdParty = partyService.createParty(party);

        URI location = UriCreator.createUri(PARTY_DEFAULT_URL, createdParty.getPartyId());

        return ResponseEntity.created(location).build();
    }

    //파티 참가
    @RequestMapping(value = "/v1/parties/{party-id}", method = RequestMethod.POST)
    public ResponseEntity addPartyMember(@PathVariable("party-id") @Positive long partyId, Member member,
                                         @RequestParam @Positive long memberId) {
        partyService.addPartyMember(partyId, member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 파티 조회
    @RequestMapping(value = "/v1/parties/{party-id}", method = RequestMethod.GET)
    public ResponseEntity getParty(
            @PathVariable("party-id") @Positive long partyId) {
        Party party = partyService.findParty(partyId);

        return new ResponseEntity(
                new SingleResponseDto<>(
                        mapper.partyToPartyResponse(party)),
                HttpStatus.OK);
    }

    // 모든 파티 조회
    @RequestMapping(value = "/v1/parties", method = RequestMethod.GET)
    public ResponseEntity getParties(@Positive @RequestParam int page,
                                     @Positive @RequestParam int size) {
        Page<Party> pageParties = partyService.findParties(page - 1, size);
        List<Party> parties = pageParties.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.partiesToPartyResponses(parties),
                        pageParties),
                HttpStatus.OK);
    }

    // 특정 멤버가 작성한 모든 파티 조회
    @RequestMapping(value = "/v1/parties", method = RequestMethod.GET, params = {"memberId"})
    public ResponseEntity getPartiesByMember(@Positive @RequestParam long memberId,
                                             @Positive @RequestParam int page,
                                             @Positive @RequestParam int size) {
        Page<Party> pageParties = partyService.findPartiesByMember(memberId, page - 1, size);
        List<Party> parties = pageParties.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.partiesToPartyResponses(parties),
                        pageParties),
                HttpStatus.OK);
    }

    // 파티 수정
    @RequestMapping(value = "/v1/parties/{party-id}", method = RequestMethod.PATCH)
    public ResponseEntity patchParty(@PathVariable("party-id") @Positive long partyId,
                                     @Valid @RequestBody PartyDto.Patch requestBody) {
        requestBody.setPartyId(partyId);
        Party party = mapper.partyPatchDtoToParty(requestBody);
        Party updatedParty = partyService.updateParty(party);

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.partyToPartyResponse(updatedParty)), HttpStatus.OK);
    }

    // 파티 삭제
    @RequestMapping(value = "/v1/parties/{party-id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteParty(@PathVariable("party-id") @Positive long partyId) {
        partyService.deleteParty(partyId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
