package com.busuu.app.entities.post;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.User;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post")
@Data
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

}
