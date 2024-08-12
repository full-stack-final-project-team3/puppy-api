package com.yp.puppy.api.entity.shop;

import com.yp.puppy.api.dto.request.shop.TreatsDetailPicDto;
import com.yp.puppy.api.dto.request.shop.TreatsPicDto;
import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import com.yp.puppy.api.util.FileUtil;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

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
    @Enumerated(EnumType.STRING)
    @Column
    private TreatsAgeType treatsAgeType;

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
        DRY, WET, GUM, KIBBLE, SUPPS
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

    public enum TreatsAgeType {
        BABY,
        OLD,
        ALL;
    }

    public void changeTreats(TreatsSaveDto dto, String uploadDir) {
        this.treatsTitle = dto.getTitle();
        this.treatsWeight = dto.getTreatsWeight();
        this.treatsType = dto.getTreatsType();
        this.treatsAgeType = dto.getTreatsAgeType();
        this.dogSize = dto.getDogSize();
        this.allergieList = dto.getAllergieList();
        updateImages(dto.getTreatsPics(), uploadDir);
        updateDetailImages(dto.getTreatsDetailPics(), uploadDir);
    }

    public void updateImages(List<TreatsPicDto> newImageDtos, String uploadDir) {

        // 기존 이미지를 맵으로 저장
        Map<String, TreatsPic> existingImages = this.treatsPics
                .stream()
                .collect(Collectors.toMap(TreatsPic::getTreatsPic, image -> image));

        List<TreatsPic> updatedImages = new ArrayList<>();

        for (TreatsPicDto newImageDto : newImageDtos) {
            if (newImageDto.getTreatsPicFile() != null) {
                TreatsPic newImage = new TreatsPic();
                String uploadPath = FileUtil.uploadFile(uploadDir, newImageDto.getTreatsPicFile()); // 파일 업로드
                newImage.setTreatsPic(uploadPath); // 파일 경로 저장
                newImage.setTreats(this); // 관계 설정

                // 새 이미지를 기존 이미지와 비교하여 유지하거나 추가
                if (existingImages.containsKey(newImage.getTreatsPic())) {
                    // 이미 존재하는 이미지를 유지
                    updatedImages.add(existingImages.get(newImage.getTreatsPic()));
                } else {
                    // 새 이미지 추가
                    updatedImages.add(newImage);
                }
            }
        }

        // 기존 리스트를 업데이트된 리스트로 교체
        this.treatsPics.clear();
        this.treatsPics.addAll(updatedImages);
    }

    public void updateDetailImages(List<TreatsDetailPicDto> newDetailImageDtos, String uploadDir) {
        // 기존 이미지를 맵으로 저장
        Map<String, TreatsDetailPic> existingImages = this.treatsDetailPics
                .stream()
                .collect(Collectors.toMap(TreatsDetailPic::getTreatsDetailPic, image -> image));

        List<TreatsDetailPic> updatedImages = new ArrayList<>();

        for (TreatsDetailPicDto newDetailImageDto : newDetailImageDtos) {
            if (newDetailImageDto.getTreatsDetailPicFile() != null) {
                // 새 디테일 이미지 객체 생성
                TreatsDetailPic newDetailImage = new TreatsDetailPic();
                String uploadPath = FileUtil.uploadFile(uploadDir, newDetailImageDto.getTreatsDetailPicFile()); // 파일 업로드
                newDetailImage.setTreatsDetailPic(uploadPath); // 파일 경로 저장
                newDetailImage.setTreats(this); // 관계 설정

                // 새 이미지를 기존 이미지와 비교하여 유지하거나 추가
                if (existingImages.containsKey(newDetailImage.getTreatsDetailPic())) {
                    // 이미 존재하는 이미지를 유지
                    updatedImages.add(existingImages.get(newDetailImage.getTreatsDetailPic()));
                } else {
                    // 새 이미지 추가
                    updatedImages.add(newDetailImage);
                }
            }
        }

        // 기존 리스트를 업데이트된 리스트로 교체
        this.treatsDetailPics.clear();
        this.treatsDetailPics.addAll(updatedImages);
    }

}
