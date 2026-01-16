package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
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

    @OneToMany(mappedBy="parent")
    private List<Comment> children;

    protected Comment() {
        this.createdAt = LocalDateTime.now();
        this.hidden = false;
    }

    public Comment(String content, Comment parent) {
        this();
        this.content = content;
        this.parent = parent;
    }
    
}
