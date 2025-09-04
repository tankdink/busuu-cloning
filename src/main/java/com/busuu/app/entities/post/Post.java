package com.busuu.app.entities.post;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.Correction;
import com.busuu.app.entities.Language;
import com.busuu.app.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post extends BaseEntity
{
    @Id
    @Column(name = "post_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType;

    @Column(name = "subject_text")
    private String subjectText;

    @Column(name = "subject_img_name")
    private String subjectImageName;

    @Column(name = "subject_img_url")
    private String subjectImageUrl;

    @Column(name = "subject_video_name")
    private String subjectVideoName;

    @Column(name = "subject_video_url")
    private String subjectVideoUrl;

    @Column(name = "post_audio_name")
    private String postAudioName;

    @Column(name = "post_audio_url")
    private String postAudioUrl;

    @Column(name = "post_text")
    private String postText;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Correction> corrections = new ArrayList<>();

}
