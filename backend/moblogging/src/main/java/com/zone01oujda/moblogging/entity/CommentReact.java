package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;

import com.zone01oujda.moblogging.react.enums.ReactType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "comment_reacts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
public class CommentReact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected CommentReact() {
        this.createdAt = LocalDateTime.now();
    }

    public CommentReact(User user, Comment comment, ReactType type) {
        this();
        this.user = user;
        this.comment = comment;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Comment getComment() {
        return comment;
    }

    public ReactType getType() {
        return type;
    }

    public void setType(ReactType type) {
        this.type = type;
    }
}
