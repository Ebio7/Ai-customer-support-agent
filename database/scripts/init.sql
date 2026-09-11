-- Initialize database schema for Hiver Support Agent
-- This script creates the necessary tables and indexes

CREATE DATABASE IF NOT EXISTS hiver_support;
USE hiver_support;

-- Conversations table
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tweet_id VARCHAR(255) NOT NULL,
    author_id VARCHAR(255) NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    brand VARCHAR(255) NOT NULL,
    inbound_or_outbound VARCHAR(50) NOT NULL,
    response_tweet_id VARCHAR(255),
    response_text TEXT,
    intent VARCHAR(100),
    escalation_decision VARCHAR(20),
    escalation_reason TEXT,
    drafted_reply TEXT,
    confidence_score DOUBLE,
    similar_responses TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_golden_set BOOLEAN DEFAULT FALSE,
    human_label TEXT,
    INDEX idx_brand (brand),
    INDEX idx_intent (intent),
    INDEX idx_golden_set (is_golden_set),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data for testing (optional)
-- INSERT INTO conversations (tweet_id, author_id, author_name, text, brand, inbound_or_outbound, response_tweet_id, response_text, intent)
-- VALUES 
-- ('123456789', 'user123', 'John Doe', 'My package is delayed, when will it arrive?', 'AmazonSupport', 'inbound', '987654321', 'I apologize for the delay. Your package is expected to arrive by Friday. Let me know if you need further assistance.', 'shipping_delay');