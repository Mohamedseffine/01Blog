package com.zone01oujda.moblogging.entity;

import java.time.LocalDateTime;

import com.zone01oujda.moblogging.notification.enums.NotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User receiver;

    private Boolean state ;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String content ;

    private LocalDateTime createdAt;

    protected  Notification() {
        createdAt = LocalDateTime.now();
        state = false;
    }

    public Notification(NotificationType type, String content, User receiver) {
        this();
        this.receiver = receiver;
        this.type = type;
        this.content = content;
    }

}
