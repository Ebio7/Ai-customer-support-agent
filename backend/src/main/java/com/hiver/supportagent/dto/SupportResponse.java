package com.hiver.supportagent.dto;

public class SupportResponse {
    private String intent;
    private String draftedReply;
    private String escalationDecision; // "AUTO_HANDLE" or "ESCALATE"
    private String escalationReason;
    private Double confidenceScore;
    private String[] similarResponses;
    private String requestId;
    private Long conversationId;

    public SupportResponse() {}

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public String getDraftedReply() { return draftedReply; }
    public void setDraftedReply(String draftedReply) { this.draftedReply = draftedReply; }

    public String getEscalationDecision() { return escalationDecision; }
    public void setEscalationDecision(String escalationDecision) { this.escalationDecision = escalationDecision; }

    public String getEscalationReason() { return escalationReason; }
    public void setEscalationReason(String escalationReason) { this.escalationReason = escalationReason; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String[] getSimilarResponses() { return similarResponses; }
    public void setSimilarResponses(String[] similarResponses) { this.similarResponses = similarResponses; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}