package com.busuu.app.entities.topics;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.notifications.Notification;
import com.busuu.app.entities.posts.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Topic extends BaseEntity
{
    @Id
    @Column(name = "topic_id")
    private String id;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_name")
    private String videoName;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_description")
    private String videoDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "topic_type")
    private TopicType topicType;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TopicCategory topicCategory;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();


}
