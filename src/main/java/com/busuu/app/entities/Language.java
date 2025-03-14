package com.busuu.app.entities;

import com.busuu.app.entities.progresses.CourseProgress;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "language")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Language extends BaseEntity{
    @Id
    @Column(name = "language_id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "flag_icon_url")
    private String flagIconUrl;

    @Column(name = "flag_icon_name")
    private String flagIconName;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLanguage> userLanguages = new HashSet<>();

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grammar> grammars;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseProgress> courseProgresses;
}
