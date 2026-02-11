package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import jakarta.persistence.CascadeType;
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

    @Column(columnDefinition = "TEXT")
    private String mediaUrl;

    private LocalDateTime createdAt;

    private Boolean hidden;

    @NotNull(message="the user is null")
    @ManyToOne(fetch=FetchType.LAZY , optional=false)
    @JoinColumn(name = "user_id", nullable=false)
    private User creator;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<React> reacts;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    protected  Post() {
        createdAt = LocalDateTime.now();
        hidden = false;
    }

    public Post(String subject, String content, String mediaUrl, PostVisibility visibility, String title) {
        this();
        this.subject = subject;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.visibility = visibility;
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden){
        this.hidden = hidden;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PostVisibility getVisibility() {
        return visibility;
    }

    public void setPostVisibility(PostVisibility visibility) {
        this.visibility = visibility;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<React> getReacts() {
        return reacts;
    }

    public void setReacts(List<React> reacts) {
        this.reacts = reacts;
    }


    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
