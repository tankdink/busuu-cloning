package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "word_example")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordExample extends BaseEntity {

    @Id
    @Column(name = "word_example_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "original_text")
    private String originalText;

    @Column(name = "translate_text")
    private String translateText;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "audio_name")
    private String audioName;
}
