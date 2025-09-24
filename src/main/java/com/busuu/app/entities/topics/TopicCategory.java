package com.busuu.app.entities.topics;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.enums.TopicType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topic_category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TopicCategory extends BaseEntity
{

    @Id
    @Column(name = "category_id")
    private String id;

    @Column(name = "category_name")
    private String categoryName;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "topicCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topics = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "topic_type")
    private TopicType topicType;

}
