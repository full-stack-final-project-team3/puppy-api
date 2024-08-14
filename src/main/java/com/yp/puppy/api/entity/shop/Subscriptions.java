package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString(exclude = {"order", "user"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Entity
@Table(name = "subscriptions")
public class Subscriptions {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "subscriptions_id")
    private String id;

    @Column(nullable = false)
    private LocalDateTime subscriptionsStartDate;

    @Column(nullable = false)
    private LocalDateTime subscriptionsEndDate;

    @OneToOne
    @JoinColumn(name = "bundle_id", referencedColumnName = "bundle_id")
    private Bundle bundle;

//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    @JsonIgnore
//    private User user;

}
