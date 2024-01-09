package com.ssdam.party.entity;

import com.ssdam.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PartyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long partyMemberId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PARTY_ID")
    private Party party;

    public PartyMember(Member member, Party party) {
        this.member = member;
        this.party = party;
    }

    public void addMember(Member member) {
        this.member = member;
        if (!this.member.getPartyMembers().contains(this)) {
            this.member.getPartyMembers().add(this);
        }
    }

    public void addParty(Party party) {
        this.party = party;
        if (!this.party.getPartyMembers().contains(this)) {
            this.party.getPartyMembers().add(this);
        }
    }
}
