package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.zone01oujda.moblogging.entity.CommentReact;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(columnDefinition= "TEXT", nullable= false)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "parent")
    private Comment parent;

    private Boolean hidden;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private Boolean modified;
    
    @OneToMany(mappedBy="parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentReact> reacts;

    protected Comment() {
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
        this.modifiedAt = LocalDateTime.now();
        this.modified = false;
    }

    public Comment(String content, Comment parent) {
        this();
        this.content = content;
        this.parent = parent;
    }

    public Comment(String content, User creator, Post post) {
        this();
        this.content = content;
        this.creator = creator;
        this.post = post;
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

    public LocalDateTime getCreatedAt() {
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

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt =  modifiedAt;
    }

    public LocalDateTime getModifiedAt() {
        return this.modifiedAt;
    }

    public void setModified(Boolean modified) {
        this.modified =  modified;
    }

    public Boolean getModified() {
        return this.modified;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }

    public List<CommentReact> getReacts() {
        return reacts;
    }

    public void setReacts(List<CommentReact> reacts) {
        this.reacts = reacts;
    }
}
