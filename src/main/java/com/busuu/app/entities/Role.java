package com.busuu.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends BaseEntity{
    @Id
    @Column(name = "role_id")
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                CascadeType.PERSIST, CascadeType.DETACH,
                CascadeType.MERGE, CascadeType.REFRESH
            }
    )
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> users;
}
