package com.hiver.supportagent.dto;

public class SupportRequest {
    private String customerMessage;
    private String customerId;
    private String customerName;
    private String tweetId;

    public SupportRequest() {}

    public String getCustomerMessage() { return customerMessage; }
    public void setCustomerMessage(String customerMessage) { this.customerMessage = customerMessage; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getTweetId() { return tweetId; }
    public void setTweetId(String tweetId) { this.tweetId = tweetId; }
}