package com.hiver.supportagent.service;

import com.hiver.supportagent.entity.Conversation;
import com.hiver.supportagent.repository.ConversationRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataIngestionService {
    
    private static final Logger log = LoggerFactory.getLogger(DataIngestionService.class);
    
    @Value("${app.brand.name}")
    private String targetBrand;
    
    @Value("${app.data.sample.size}")
    private int sampleSize;
    
    private final ConversationRepository conversationRepository;
    
    public DataIngestionService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }
    
    public String ingestCSVData(MultipartFile file) {
        try {
            log.info("Starting data ingestion from file: {}", file.getOriginalFilename());
            
            List<Conversation> conversations = new ArrayList<>();
            int processedCount = 0;
            int brandCount = 0;
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                         .withFirstRecordAsHeader()
                         .withIgnoreHeaderCase()
                         .withTrim())) {
                
                for (CSVRecord record : csvParser) {
                    if (processedCount >= sampleSize) {
                        break;
                    }
                    
                    try {
                        String brand = getSafeValue(record, "brand");
                        
                        // Filter for target brand or process all if not specified
                        if (targetBrand != null && !targetBrand.isEmpty() && !brand.equalsIgnoreCase(targetBrand)) {
                            continue;
                        }
                        
                        Conversation conversation = new Conversation();
                        conversation.setTweetId(getSafeValue(record, "tweet_id"));
                        conversation.setAuthorId(getSafeValue(record, "author_id"));
                        conversation.setAuthorName(getSafeValue(record, "author_name"));
                        conversation.setText(getSafeValue(record, "text"));
                        conversation.setBrand(brand);
                        conversation.setInboundOrOutbound(getSafeValue(record, "inbound_or_outbound"));
                        conversation.setResponseTweetId(getSafeValue(record, "response_tweet_id"));
                        conversation.setResponseText(getSafeValue(record, "response_text"));
                        
                        conversations.add(conversation);
                        brandCount++;
                        processedCount++;
                        
                        // Batch save every 100 records
                        if (conversations.size() >= 100) {
                            conversationRepository.saveAll(conversations);
                            log.info("Saved batch of {} conversations. Total processed: {}", 
                                    conversations.size(), processedCount);
                            conversations.clear();
                        }
                        
                    } catch (Exception e) {
                        log.warn("Error processing record {}: {}", processedCount, e.getMessage());
                    }
                }
                
                // Save remaining records
                if (!conversations.isEmpty()) {
                    conversationRepository.saveAll(conversations);
                    log.info("Saved final batch of {} conversations", conversations.size());
                }
                
            }
            
            String message = String.format("Data ingestion completed. Processed %d records for brand %s", 
                    brandCount, targetBrand);
            log.info(message);
            return message;
            
        } catch (Exception e) {
            log.error("Error during data ingestion", e);
            throw new RuntimeException("Failed to ingest CSV data: " + e.getMessage(), e);
        }
    }
    
    private String getSafeValue(CSVRecord record, String columnName) {
        try {
            String value = record.get(columnName);
            return value != null ? value.trim() : "";
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
    
    public long getConversationCount() {
        return conversationRepository.count();
    }
    
    public long getBrandConversationCount() {
        return conversationRepository.countByBrand(targetBrand);
    }
}