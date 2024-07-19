package com.yp.puppy.api.entity.shop;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "treats")
public class Treats {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "treats_id")
    private String id;

    @Column(nullable = false)
    private String treatsTitle; // 패키지 이름

    @Column(nullable = false)
    private TreatsType treatsType;

    private TreatsAge treatsAge;

    private int treatsStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treats_id")
    private Bundle bundle;

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "treats_id")
    private List<TreatsPic> treatsPic;

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "treats_id")
    private List<TreatsDetailPic> treatsDetailPics;

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "treats_id")
    private Review reviews;

    private enum TreatsType {
        DRY, WET
    }

    private enum TreatsAge {
        BABY, ADULT
    }
}
