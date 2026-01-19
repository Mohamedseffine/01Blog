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
@Table(name = "reacts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
public class React {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactType type;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    protected React() {
        this.createdAt = LocalDateTime.now();
    }
    
    public React(User user, Post post, ReactType type) {
        this();
        this.user = user;
        this.post = post;
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Post getPost() {
        return post;
    }
    
    public void setPost(Post post) {
        this.post = post;
    }
    
    public ReactType getType() {
        return type;
    }
    
    public void setType(ReactType type) {
        this.type = type;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
