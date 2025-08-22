package com.team.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="firstUser")
    private User firstUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="secondUser")
    private User secondUser;


    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER)
    private List<Message> messages;

    public Match(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.messages = new ArrayList<>();
    }

    @Column(name = "conversation_started", nullable = false)
    private boolean conversationStarted = false;

}
