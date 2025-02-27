package com.busuu.app.entities.questions;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.Lesson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question extends BaseEntity {

    @Id
    @Column(name = "question_id")
    private String id;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "mark")
    private Integer mark;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "is_completed", columnDefinition = "false")
    private Boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    public boolean isCompleted() {
        return isCompleted;
    }
}
