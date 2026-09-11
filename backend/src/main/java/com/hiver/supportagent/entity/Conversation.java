package com.hiver.supportagent.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class Conversation {

    public Conversation() {}

    public Conversation(String tweetId, String authorId, String authorName, String text, String brand, String inboundOrOutbound) {
        this.tweetId = tweetId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.text = text;
        this.brand = brand;
        this.inboundOrOutbound = inboundOrOutbound;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tweetId;
    
    @Column(nullable = false)
    private String authorId;
    
    @Column(nullable = false)
    private String authorName;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private String inboundOrOutbound;
    
    @Column(nullable = false)
    private String responseTweetId;
    
    @Column(columnDefinition = "TEXT")
    private String responseText;
    
    @Column(length = 100)
    private String intent;
    
    @Column(length = 20)
    private String escalationDecision;
    
    @Column(columnDefinition = "TEXT")
    private String escalationReason;
    
    @Column(columnDefinition = "TEXT")
    private String draftedReply;
    
    @Column(columnDefinition = "DOUBLE")
    private Double confidenceScore;
    
    @Column(columnDefinition = "TEXT")
    private String similarResponses; // JSON array of similar historical responses
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isGoldenSet = false;
    
    @Column(columnDefinition = "TEXT")
    private String humanLabel; // For golden set validation

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTweetId() { return tweetId; }
    public void setTweetId(String tweetId) { this.tweetId = tweetId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getInboundOrOutbound() { return inboundOrOutbound; }
    public void setInboundOrOutbound(String inboundOrOutbound) { this.inboundOrOutbound = inboundOrOutbound; }

    public String getResponseTweetId() { return responseTweetId; }
    public void setResponseTweetId(String responseTweetId) { this.responseTweetId = responseTweetId; }

    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public String getEscalationDecision() { return escalationDecision; }
    public void setEscalationDecision(String escalationDecision) { this.escalationDecision = escalationDecision; }

    public String getEscalationReason() { return escalationReason; }
    public void setEscalationReason(String escalationReason) { this.escalationReason = escalationReason; }

    public String getDraftedReply() { return draftedReply; }
    public void setDraftedReply(String draftedReply) { this.draftedReply = draftedReply; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getSimilarResponses() { return similarResponses; }
    public void setSimilarResponses(String similarResponses) { this.similarResponses = similarResponses; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsGoldenSet() { return isGoldenSet; }
    public void setIsGoldenSet(Boolean isGoldenSet) { this.isGoldenSet = isGoldenSet; }

    public String getHumanLabel() { return humanLabel; }
    public void setHumanLabel(String humanLabel) { this.humanLabel = humanLabel; }
}