package com.ssdam.like.entity;

import com.ssdam.comment.entity.Comment;
import com.ssdam.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "LIKES")
@Getter
@Setter
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long likeId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    public Like(Member member, Comment comment) {
        this.member = member;
        this.comment = comment;
    }

    public void addMember(Member member) {
        this.member = member;
        if (!this.member.getLikes().contains(this)) {
            this.member.getLikes().add(this);
        }
    }

    public void addComment(Comment comment) {
        this.comment = comment;
        if (!this.comment.getLikes().contains(this)) {
            this.comment.getLikes().add(this);
        }
    }
}
