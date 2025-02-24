package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

    @Id
    @Column(name = "token_id")
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "revoked")
    private boolean revoked;

    @Column(name = "expired")
    private boolean expired;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;

    @Column(name = "is_mobile")
    private boolean isMobile;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "user_id")
    private User user;
}
