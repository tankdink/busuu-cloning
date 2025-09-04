package com.busuu.app.entities.topic;

import com.busuu.app.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topic_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicCategory extends BaseEntity
{
    @Id
    @Column(name = "category_id")
    private String id;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(mappedBy = "topicCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topics = new ArrayList<>();


}
