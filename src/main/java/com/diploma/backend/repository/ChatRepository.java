package com.diploma.backend.repository;

import com.diploma.backend.Entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.diploma.backend.Entity.Chat;
import com.diploma.backend.Entity.ChatMessage;
import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.participant1.id = :userId OR c.participant2.id = :userId")
    List<Chat> findByParticipantId(@Param("userId") Long userId);

    @Query("SELECT c FROM Chat c WHERE " +
            "(c.participant1.id = :u1 AND c.participant2.id = :u2) OR " +
            "(c.participant1.id = :u2 AND c.participant2.id = :u1)")
    Optional<Chat> findExistingChat(@Param("u1") Long u1, @Param("u2") Long u2);
}