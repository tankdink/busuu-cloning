package com.busuu.app.entities;

import com.busuu.app.entities.enums.FriendShipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "friend_ship")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendShip extends BaseEntity {

    @Id
    @Column(name = "friend_ship_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "from_user")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user")
    private User toUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private FriendShipStatus status = FriendShipStatus.PENDING;
}
