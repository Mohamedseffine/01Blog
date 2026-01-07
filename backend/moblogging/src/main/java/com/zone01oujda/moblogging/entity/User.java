package com.zone01oujda.moblogging.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zone01oujda.moblogging.user.enums.Gender;
import com.zone01oujda.moblogging.user.enums.ProfileType;
import com.zone01oujda.moblogging.user.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private String firstName;

    @Column(nullable= false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; 

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender; 

    @Enumerated(EnumType.STRING)
    private Role role; 

    @Enumerated(EnumType.STRING)
    private ProfileType profileType; 

    private Boolean blocked = false;
    private Boolean banned = false;

    private LocalDateTime createdAt;

    // // Relations
    @OneToMany(mappedBy = "creator")
    private List<Post> posts;

    @OneToMany(mappedBy = "receiver")
    private List<Notification> notifications;

    @OneToMany( mappedBy = "creator")
    private List<Comment> comments;


    // Constructors
    protected User() {
        this.createdAt = LocalDateTime.now();
        this.blocked = false;
        this.banned = false;
    }

    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.USER; 
        this.profileType = ProfileType.PUBLIC;
        this.posts = new  ArrayList<>();
    }

    
}