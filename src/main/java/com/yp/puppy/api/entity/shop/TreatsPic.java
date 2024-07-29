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
@Table(name = "treats_pic")
public class TreatsPic {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "treats_pic_id")
    private String id;

    @Column(nullable = false)
    private String treatsPic;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treats_id")
    @JsonIgnore// Child side
    private Treats treats;

    // 추가적인 유효성 검사 메서드
    public void setTreatsPic(String treatsPic) {
        if (treatsPic == null || treatsPic.isEmpty()) {
            throw new IllegalArgumentException("Image URI cannot be null or empty");
        }
        this.treatsPic = treatsPic;
    }
}
