package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "word")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Word extends BaseEntity {

    @Id
    @Column(name = "word_id")
    private String id;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "translation")
    private String translation;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_name")
    private String videoName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_name")
    private String imageName;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<WordExample> examples;
}
