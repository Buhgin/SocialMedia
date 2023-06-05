package com.boris.dao.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import lombok.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Activity> activities;
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<FriendRequest> sentFriendRequests;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<FriendRequest> receivedFriendRequests;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Message> receivedMessages;
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    public User(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User that = (User) o;
        return getUsername() != null && Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
