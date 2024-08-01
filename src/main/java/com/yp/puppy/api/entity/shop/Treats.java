package com.yp.puppy.api.entity.shop;

import com.yp.puppy.api.dto.request.shop.TreatsDetailPicDto;
import com.yp.puppy.api.dto.request.shop.TreatsPicDto;
import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import com.yp.puppy.api.entity.user.Dog;
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
@ToString(exclude = {"treatsPics", "treatsDetailPics", "reviews", "bundle"})
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DogSize dogSize;

    @Setter
    private int treatsStock;

    @Setter
    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatsPic> treatsPics = new ArrayList<>(); // 변경: TreatsPicDto -> TreatsPic

    @Setter
    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreatsDetailPic> treatsDetailPics = new ArrayList<>(); // 변경: TreatsDetailPicDto -> TreatsDetailPic

    @OneToMany(mappedBy = "treats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<Allergic> allergieList = new ArrayList<>();

    public enum TreatsType {
        DRY, WET
    }

//    public enum DogSize {
//        SMALL, MEDIUM, LARGE
//    }

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

    public void updateImages(List<TreatsPicDto> newImageDtos) {
        // 이미지 DTO에서 TreatsPic으로 변환
        Map<String, TreatsPic> existingImages = this.treatsPics
                .stream()
                .collect(Collectors.toMap(TreatsPic::getTreatsPic, image -> image));

        List<TreatsPic> updatedImages = new ArrayList<>();

        for (TreatsPicDto newImageDto : newImageDtos) {
            TreatsPic newImage = new TreatsPic();
            newImage.setTreatsPic(newImageDto.getTreatsPicFile().getOriginalFilename()); // 파일 이름 저장
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

    public void updateDetailImages(List<TreatsDetailPicDto> newDetailImageDtos) {
        // 이미지 DTO에서 TreatsDetailPic으로 변환
        Map<String, TreatsDetailPic> existingImages = this.treatsDetailPics
                .stream()
                .collect(Collectors.toMap(TreatsDetailPic::getTreatsDetailPic, image -> image));

        List<TreatsDetailPic> updatedImages = new ArrayList<>();

        for (TreatsDetailPicDto newDetailImageDto : newDetailImageDtos) {
            TreatsDetailPic newDetailImage = new TreatsDetailPic();
            newDetailImage.setTreatsDetailPic(newDetailImageDto.getTreatsDetailPicFile().getOriginalFilename()); // 파일 이름 저장
            // 새 이미지를 기존 이미지와 비교하여 유지하거나 추가
            if (existingImages.containsKey(newDetailImage.getTreatsDetailPic())) {
                // 이미 존재하는 이미지를 유지
                updatedImages.add(existingImages.get(newDetailImage.getTreatsDetailPic()));
            } else {
                // 새 이미지 추가
                updatedImages.add(newDetailImage);
                newDetailImage.setTreats(this); // 관계 설정
            }
        }

        // 기존 리스트를 업데이트된 리스트로 교체
        this.treatsDetailPics.clear();
        this.treatsDetailPics.addAll(updatedImages);
    }
}
