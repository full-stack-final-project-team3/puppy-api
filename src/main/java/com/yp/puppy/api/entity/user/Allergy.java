package com.yp.puppy.api.entity.user;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@ToString(exclude = "dog")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "allergies")
public class Allergy {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "allergy_id")
    private String id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AllergicType type;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog;

    public enum AllergicType {
        BEEF, CHICKEN, CORN, DAIRY, FISH, FLAX, LAMB, PORK, TURKEY, WHEAT
    }
}