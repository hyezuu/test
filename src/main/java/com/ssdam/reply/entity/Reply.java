package com.ssdam.reply.entity;

import com.ssdam.comment.entity.Comment;
import com.ssdam.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long replyId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    public void addMember(Member member) {
        this.member = member;
        if (!this.member.getReplies().contains(this)) {
            this.member.getReplies().add(this);
        }
    }

    public void addComment(Comment comment) {
        this.comment = comment;
        if (comment.getReply() != this) {
            comment.setReply(this);
        }
    }
}
