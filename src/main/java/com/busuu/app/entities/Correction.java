package com.busuu.app.entities;

import com.busuu.app.entities.post.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "correction")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Correction extends BaseEntity
{
    @Id
    @Column(name = "correction_id")
    private String id;

    @Column(name = "correction_audio_name")
    private String correctionAudioName;

    @Column(name = "correction_audio_url")
    private String correctionAudioUrl;

    @Column(name = "correction_text")
    private String correctionText;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "dislike_count")
    private Integer dislikeCount = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
