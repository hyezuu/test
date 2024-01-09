package com.ssdam.party.service;

import com.ssdam.exception.BusinessLogicException;
import com.ssdam.exception.ExceptionCode;
import com.ssdam.member.entity.Member;
import com.ssdam.party.entity.Party;
import com.ssdam.party.entity.PartyMember;
import com.ssdam.party.repository.PartyMemberRepository;
import com.ssdam.party.repository.PartyRepository;
import com.ssdam.party.weather.WeatherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PartyService {
    private final PartyRepository partyRepository;
    private final WeatherService weatherService;

    private final PartyMemberRepository partyMemberRepository;

    public PartyService(PartyRepository partyRepository, WeatherService weatherService, PartyMemberRepository partyMemberRepository) {
        this.partyRepository = partyRepository;
        this.weatherService = weatherService;
        this.partyMemberRepository = partyMemberRepository;
    }

    public Party createParty(Party party) {
        party.setWeather(weatherService.getWeather(party.getLatitude(), party.getLongitude(),party.getMeetingDate()));
        return partyRepository.save(party);
    }

    @Transactional(readOnly = true)
    public Party findParty(long partyId) {
        return findVerifiedParty(partyId);
    }

    @Transactional(readOnly = true)
    public Page<Party> findParties(int page, int size) {
        return partyRepository.findAll(PageRequest.of(page, size,
                Sort.by("partyId").descending()));
    }

    // 특정 멤버가 작성한 모든 모임 조회
    @Transactional(readOnly = true)
    public Page<Party> findPartiesByMember(long memberId, int page, int size) {
        List<Party> parties = partyRepository.findByPartyMembers_Member_MemberId(memberId);
        Page<Party> pageParties =
                new PageImpl<>(parties,
                        PageRequest.of(page, size,
                                Sort.by("createdAt").descending()), parties.size());
        return pageParties;
    }

    // 글을 등록한 사람만 수정할 수 있게 권한 추가 해야함
    public Party updateParty(Party party) {
        Party findParty = findVerifiedParty(party.getPartyId());

        Optional.ofNullable(party.getMeetingDate())
                .ifPresent(findParty::setMeetingDate);
        Optional.ofNullable(party.getLongitude())
                .ifPresent(findParty::setLongitude);
        Optional.ofNullable(party.getLatitude())
                .ifPresent(findParty::setLatitude);
        Optional.ofNullable(party.getAddress())
                .ifPresent(findParty::setAddress);
        Optional.ofNullable(party.getTitle())
                .ifPresent(findParty::setTitle);
        Optional.ofNullable(party.getContent())
                .ifPresent(findParty::setContent);
        Optional.ofNullable(party.getMaxCapacity())
                .ifPresent(findParty::setMaxCapacity);
        Optional.ofNullable(party.getCurrentCapacity())
                .ifPresent(findParty::setCurrentCapacity);
        Optional.ofNullable(party.getPartyStatus())
                .ifPresent(findParty::setPartyStatus);

        return partyRepository.save(findParty);
    }

    public void deleteParty(long partyId) {
        Party findParty = findVerifiedParty(partyId);

        partyRepository.delete(findParty);
    }

    public Party findVerifiedParty(long partyId) {
        Optional<Party> optionalParty =
                partyRepository.findById(partyId);
        Party findParty =
                optionalParty.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.PARTY_NOT_FOUND));
        return findParty;
    }

    //파티 참가, 권한 추가 필요
    public void addPartyMember(long partyId, Member member) {
        Party party = findVerifiedParty(partyId);
        if (!partyMemberRepository.existsByMemberAndParty(member, party)) {
            party.setCurrentCapacity(party.getCurrentCapacity() + 1);
            partyMemberRepository.save(new PartyMember(member, party));
        } else {
            party.setCurrentCapacity(party.getCurrentCapacity() - 1);
            partyMemberRepository.deleteByMemberAndParty(member, party);
        }
    }
}
