package com.yp.puppy.api.entity.shop;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@ToString(exclude = "treats")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "treats_pic")
public class TreatsPic {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "treats_pic_id")
    private String id;

    private String treatsPic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treats_id")
    private Treats treats;

}
