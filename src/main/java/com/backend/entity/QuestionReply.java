package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.QuestionStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "QuestionReplies")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "reply_text")
    private String replyText;
    
    @Column(name = "images")
    private String images;

    @CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

    @Column(name = "is_accepted")
    private Boolean isAccepted;
    
    @ManyToOne
    @JoinColumn(name = "parent_reply_id")
    @JsonIgnore
    private QuestionReply parentReply;

    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionReply> childReplies;

}
