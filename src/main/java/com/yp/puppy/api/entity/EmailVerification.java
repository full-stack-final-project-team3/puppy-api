package com.yp.puppy.api.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_email_verification")
public class EmailVerification {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "verification_id")
    private String id;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
