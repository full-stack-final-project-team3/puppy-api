package com.yp.puppy.api.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_notices")
// 알림 테이블 만듬. (0801)
public class UserNotice {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "notice_id")
    private String id;

    @Column(nullable = false)
    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 읽었냐? 안읽었냐
    private boolean isClicked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-notices")
    private User user;
}