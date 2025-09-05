package com.busuu.app.entities.topics;

import com.busuu.app.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

}
