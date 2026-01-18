package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(columnDefinition= "TEXT", nullable= false)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "parent")
    private Comment parent;

    private Boolean hidden;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy="parent")
    private List<Comment> children;

    protected Comment() {
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
        this.modifiedAt = null;
    }

    public Comment(String content, Comment parent) {
        this();
        this.content = content;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Comment getParent() {
        return parent;
    }

    public User getCreator() {
        return creator;
    }
    public Post getPost() {
        return post;
    }

    public Boolean getHidden() {
        return hidden;
    }

    private LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    public void setPost(Post post) {
        this.post =  post;
    }

    public void setHidden(Boolean hidden) {
        this.hidden =  hidden;
    }

    private void setModifiededAt(LocalDateTime modifiedAt) {
        this.modifiedAt =  modifiedAt;
    }


}
