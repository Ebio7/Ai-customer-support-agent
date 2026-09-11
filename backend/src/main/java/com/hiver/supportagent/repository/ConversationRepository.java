package com.hiver.supportagent.repository;

import com.hiver.supportagent.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    List<Conversation> findByBrand(String brand);
    
    List<Conversation> findByBrandAndIntent(String brand, String intent);
    
    List<Conversation> findByIsGoldenSetTrue();
    
    @Query("SELECT DISTINCT c.intent FROM Conversation c WHERE c.intent IS NOT NULL")
    List<String> findDistinctIntents();
    
    @Query("SELECT c FROM Conversation c WHERE c.brand = :brand AND c.intent = :intent AND c.responseText IS NOT NULL ORDER BY c.createdAt DESC")
    List<Conversation> findSimilarResponses(@Param("brand") String brand, @Param("intent") String intent);
    
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.brand = :brand")
    Long countByBrand(@Param("brand") String brand);
    
    @Query("SELECT c FROM Conversation c WHERE c.text LIKE %:keyword%")
    List<Conversation> searchByText(@Param("keyword") String keyword);
}