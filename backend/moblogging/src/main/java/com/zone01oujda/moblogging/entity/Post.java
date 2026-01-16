package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String subject;

    @Enumerated(EnumType.STRING)
    private PostVisibility visibility;

    private String mediaUrl;
    private String mediaType; 

    private LocalDateTime createdAt;

    private Boolean hidden;

    @NotNull(message="the user is null")
    @ManyToOne(fetch=FetchType.LAZY , optional=false)
    @JoinColumn(name = "user_id", nullable=false)
    private User creator;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    protected  Post() {
        createdAt = LocalDateTime.now();
        hidden = false;
    }

    public Post(String subject, String content, String mediaUrl, String mediaType, PostVisibility visibility, String title) {
        this();
        this.subject = subject;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.visibility = visibility;
        this.title = title;
    }
}