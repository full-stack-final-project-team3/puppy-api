package com.yp.puppy.api.entity.shop;

import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"user", "bundles"})
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

//    @Column(nullable = false)
//    private int amount;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private CartStatus cartStatus;

//    @Column(nullable = false)
    private SubsType subsType;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bundle> bundles;

    public enum CartStatus {
        PENDING, SHIPPED, CANCELLED
    }

    public enum SubsType {
        ONE, MONTH3, MONTH6
    }

//    public void defaultCart(Cart cart) {
//
//        this.cartStatus = CartStatus.PENDING;
//
//    }
}
