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
@Table(name = "user")
public class User {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "user_id")
    private String id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(length = 500)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    private LocalDateTime createAt; // 회원가입 시간

    @Setter
    @Column(nullable = false)
    private boolean emailVerified;

    public void confirm(String password) {
        this.password = password;
        this.createAt = LocalDateTime.now();
    }
}
