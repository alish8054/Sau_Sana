package com.diploma.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant1_id")
    private User participant1;

    @ManyToOne
    @JoinColumn(name = "participant2_id")
    private User participant2;

    private String lastMessage;

    private LocalDateTime lastMessageTime;
}