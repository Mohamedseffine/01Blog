package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bans")
public class Ban {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime unbannedAt;
    
    @Column(nullable = false)
    private Boolean isPermanent;
    
    protected Ban() {
        this.createdAt = LocalDateTime.now();
        this.isPermanent = false;
    }
    
    public Ban(User user, User admin, String reason) {
        this();
        this.user = user;
        this.admin = admin;
        this.reason = reason;
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
    
    public User getAdmin() {
        return admin;
    }
    
    public void setAdmin(User admin) {
        this.admin = admin;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUnbannedAt() {
        return unbannedAt;
    }
    
    public void setUnbannedAt(LocalDateTime unbannedAt) {
        this.unbannedAt = unbannedAt;
    }
    
    public Boolean getIsPermanent() {
        return isPermanent;
    }
    
    public void setIsPermanent(Boolean isPermanent) {
        this.isPermanent = isPermanent;
    }
}
