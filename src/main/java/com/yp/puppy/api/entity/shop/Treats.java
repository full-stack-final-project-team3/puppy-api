package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yp.puppy.api.entity.user.Dog.*;

@Getter
@Setter
@ToString(exclude = {"treatsPic", "treatsDetailPics", "reviews", "bundle"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "treats")
public class Treats {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "treats_id")
    private String id;

    @Setter
    @Column(nullable = false)
    private String treatsTitle; // 간식 이름

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatsType treatsType;

    @Setter
    @Column(nullable = false)
    private int treatsWeight;

    @Setter
    @Column(nullable = false)
    private DogSize dogSize;

    @Setter
    private int treatsStock;

    @Setter
    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatsPic> treatsPics = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatsDetailPic> treatsDetailPics = new ArrayList<>();

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<Allergic> allergieList = new ArrayList<>();

    public enum TreatsType {
        DRY, WET
    }

    public enum Allergic {
        BEEF,
        CHICKEN,
        CORN,
        DAIRY,
        FISH,
        FLAX,
        LAMB,
        PORK,
        TURKEY,
        WHEAT,
        SOY,
        RICE,
        PEANUT,
        BARLEY,
        OAT,
        POTATO,
        TOMATO,
        SALMON,
        DUCK
    }

    public void changeTreats(TreatsSaveDto dto) {

        this.treatsTitle = dto.getTitle();
        this.treatsWeight = dto.getTreatsWeight();
        this.treatsType = dto.getTreatsType();
        this.dogSize = dto.getDogSize();
        updateImages(dto.getTreatsPics());
        updateDetailImages(dto.getTreatsDetailPics());

    }

    public void updateImages(List<TreatsPic> newImages) {
        // 이미지 식별자 즉 uri 를 사용해 매핑
        Map<String, TreatsPic> existingImages = this.treatsPics
                .stream()
                .collect(Collectors.toMap(TreatsPic::getTreatsPic, image -> image));

        List<TreatsPic> updatedImages = new ArrayList<>();

        for (TreatsPic newImage : newImages) {
            // 새 이미지를 기존 이미지와 비교하여 유지하거나 추가
            if (existingImages.containsKey(newImage.getTreatsPic())) {
                // 이미 존재하는 이미지를 유지
                updatedImages.add(existingImages.get(newImage.getTreatsPic()));
            } else {
                // 새 이미지 추가
                updatedImages.add(newImage);
                newImage.setTreats(this); // 관계 설정
            }
        }

        // 기존 리스트를 업데이트된 리스트로 교체
        this.treatsPics.clear();
        this.treatsPics.addAll(updatedImages);
    }

    public void updateDetailImages(List<TreatsDetailPic> newImages) {
        // 이미지 식별자 즉 uri 를 사용해 매핑
        Map<String, TreatsDetailPic> existingImages = this.treatsDetailPics
                .stream()
                .collect(Collectors.toMap(TreatsDetailPic::getTreatsDetailPic, image -> image));

        List<TreatsDetailPic> updatedImages = new ArrayList<>();

        for (TreatsDetailPic newImage : newImages) {
            // 새 이미지를 기존 이미지와 비교하여 유지하거나 추가
            if (existingImages.containsKey(newImage.getTreatsDetailPic())) {
                // 이미 존재하는 이미지를 유지
                updatedImages.add(existingImages.get(newImage.getTreatsDetailPic()));
            } else {
                // 새 이미지 추가
                updatedImages.add(newImage);
                newImage.setTreats(this); // 관계 설정
            }
        }

        // 기존 리스트를 업데이트된 리스트로 교체
        this.treatsDetailPics.clear();
        this.treatsDetailPics.addAll(updatedImages);
    }

}