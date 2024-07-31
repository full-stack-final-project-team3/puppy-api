package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Getter
@Setter
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

    @Column(nullable = false)
    private String treatsDetailPic; // URL

    @ManyToOne
    @JoinColumn(name = "treats_id")
    @JsonIgnore // Child side
    private Treats treats;

    // 추가적인 유효성 검사 메서드
//    public void setTreatsDetailPic(String treatsDetailPic) {
//        if (treatsDetailPic == null || treatsDetailPic.isEmpty()) {
//            throw new IllegalArgumentException("Image URI cannot be null or empty");
//        }
//        this.treatsDetailPic = treatsDetailPic;
//    }
}
