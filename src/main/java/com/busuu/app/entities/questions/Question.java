package com.busuu.app.entities.questions;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Lesson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Column(name = "request", nullable = false)
    private String request;

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "mark")
    private Integer mark;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_name")
    private String videoName;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "audio_name")
    private String audioName;

    @Column(name = "hint", columnDefinition = "TEXT")
    private String hint;

    @Column(name = "question_order")
    private Integer questionOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "show_type", nullable = false)
    private ShowType showType;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "grammar_section_id")
    private GrammarSection grammarSection;
}
