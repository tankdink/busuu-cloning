package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "grammar")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Grammar extends BaseEntity{

    @Id
    @Column(name = "grammar_id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "flag_icon_url")
    private String flagIconUrl;

    @Column(name = "flag_icon_name")
    private String flagIconName;

    @Column(name = "grammar_order")
    private Integer grammarOrder;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @OneToMany(mappedBy = "grammar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrammarSection> grammarSections;

}
