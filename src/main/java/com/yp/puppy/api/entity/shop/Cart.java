package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"user"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "cart_id")
    private String id;

    @Column(nullable = false)
    private Long totalPrice;

    private CartStatus cartStatus;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bundle> bundles;

    public enum CartStatus {
        PENDING, ORDERED, CANCELLED
    }

}
