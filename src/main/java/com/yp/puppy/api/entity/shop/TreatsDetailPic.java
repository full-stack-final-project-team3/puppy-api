package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "treats_detail_pic")
public class TreatsDetailPic {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "treats_detail_pic_id")
    private String id;

    private String treatsDetailPic;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treats_id")
    @JsonIgnore // Child side
    private Treats treats;

}
