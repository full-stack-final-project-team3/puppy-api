package com.yp.puppy.api.entity.shop;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"treatsPic", "treatsDetailPics", "reviews"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "treats")
public class Treats {

    @Id
//    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
//    @GeneratedValue(generator = "uuid-generator")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "treats_id")
    private String id;

    @Column(nullable = false)
    private String treatsTitle; // 패키지 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatsType treatsType;

    @Enumerated(EnumType.STRING)
    private TreatsAge treatsAge;

    private int treatsStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatsPic> treatsPic = new ArrayList<>();

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatsDetailPic> treatsDetailPics = new ArrayList<>();

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public enum TreatsType {
        DRY, WET
    }

    public enum TreatsAge {
        BABY, ADULT
    }
}