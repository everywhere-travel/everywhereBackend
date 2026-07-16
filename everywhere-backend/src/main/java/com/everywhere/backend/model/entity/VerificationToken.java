package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ver_id_int")
    private Long id;

    @Column(name = "ver_token_vac", nullable = false, length = 6)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usr_id_int")
    private User user;

    @Column(name = "ver_exp_tmp", nullable = false)
    private LocalDateTime expiryDate;
}
