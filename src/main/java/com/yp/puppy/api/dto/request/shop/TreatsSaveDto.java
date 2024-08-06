package com.yp.puppy.api.dto.request.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.Treats.TreatsType;
import com.yp.puppy.api.entity.shop.TreatsDetailPic;
import com.yp.puppy.api.entity.shop.TreatsPic;
import com.yp.puppy.api.util.FileUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.yp.puppy.api.entity.shop.Treats.*;
import static com.yp.puppy.api.entity.user.Dog.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class TreatsSaveDto {

    @JsonProperty("treats-title")
    private String title; // 제품 이름

    @JsonProperty("treats-type")
    private TreatsType treatsType; // 제품 타입

    @JsonProperty("treats-weight")
    private int treatsWeight; // 제품 무게

    @JsonProperty("dog-size")
    private DogSize dogSize; // 강아지 크기

    @JsonProperty("treats-age-type")
    private TreatsAgeType treatsAgeType;

    @JsonProperty("treats-allergy")
    private List<Allergic> allergieList; // 알러지 유발 항목

    @JsonProperty("treats-pics")
    private List<TreatsPicDto> treatsPics; // 제품 이미지 목록

    @JsonProperty("treats-detail-pics")
    private List<TreatsDetailPicDto> treatsDetailPics; // 제품 상세 이미지 목록

    public Treats toEntity(String rootPath) {

        Treats treats = new Treats();
        treats.setTreatsTitle(this.title);
        treats.setTreatsType(this.treatsType);
        treats.setTreatsWeight(this.treatsWeight);
        treats.setDogSize(this.dogSize);
        treats.setTreatsAgeType(this.treatsAgeType);
        treats.setAllergieList(this.allergieList);

        // 이미지 처리
        processImages(treats, this.treatsPics, this.treatsDetailPics, rootPath);

        return treats;
    }

    private void processImages(Treats treats, List<TreatsPicDto> treatsPics, List<TreatsDetailPicDto> treatsDetailPics, String rootPath) {
        processTreatsPics(treats, treatsPics, rootPath);
        processDetailPics(treats, treatsDetailPics, rootPath);
    }

    private void processTreatsPics(Treats treats, List<TreatsPicDto> treatsPicsDto, String rootPath) {
        log.info("processTreatsPics 호출: 간식 정보: {}", treats);

        if (treatsPicsDto != null) {
            log.info("간식 사진 리스트의 크기: {}", treatsPicsDto.size());

            for (TreatsPicDto picDto : treatsPicsDto) {
                log.info("Processing TreatsPicDto: {}", picDto);

                if (picDto == null) {
                    log.error("TreatsPicDto가 null입니다.");
                    continue;  // null인 경우 다음으로 넘어감
                }

                if (picDto.getTreatsPicFile() == null) {
                    log.error("간식 사진 파일이 null입니다. DTO: {}", picDto);
                    continue;  // null인 경우 다음으로 넘어감
                }

                try {
                    log.info("간식 사진 파일 처리 시작: {}", picDto.getTreatsPicFile().getOriginalFilename());
                    String uploadPath = FileUtil.uploadFile(rootPath, picDto.getTreatsPicFile());
                    log.info("간식 사진 파일 업로드 완료: {}", uploadPath);

                    // TreatsPic 객체 생성 및 설정
                    TreatsPic pic = new TreatsPic();
                    pic.setTreatsPic(uploadPath);
                    pic.setTreats(treats); // Treats와의 연관관계 설정
                    System.out.println("treats = " + treats.getTreatsPics());
                    treats.getTreatsPics().add(pic);

                    log.info("간식 사진 추가 완료: {}", pic);
                } catch (Exception e) {
                    log.error("간식 사진 처리 중 오류 발생: {}", e.getMessage(), e);
                }
            }
        } else {
            log.warn("간식 사진 리스트가 null이거나 비어있습니다.");
        }
    }


    private void processDetailPics(Treats treats, List<TreatsDetailPicDto> treatsDetailPics, String rootPath) {
        log.info("processDetailPics 호출: 간식 정보: {}", treats);

        if (treatsDetailPics != null && !treatsDetailPics.isEmpty()) {
            log.info("간식 상세 사진 리스트의 크기: {}", treatsDetailPics.size());

            for (TreatsDetailPicDto detailPicDto : treatsDetailPics) {
                if (detailPicDto == null) {
                    log.warn("간식 상세 사진 DTO가 null입니다.");
                    continue;  // null인 경우 다음으로 넘어감
                }

                if (detailPicDto.getTreatsDetailPicFile() == null) {
                    log.error("간식 상세 사진 파일이 null입니다. DTO: {}", detailPicDto);
                    throw new IllegalArgumentException("간식 상세 사진 파일이 null입니다.");
                }

                try {
                    log.info("간식 상세 사진 파일 처리 시작: {}", detailPicDto.getTreatsDetailPicFile().getOriginalFilename());
                    String uploadPath = FileUtil.uploadFile(rootPath, detailPicDto.getTreatsDetailPicFile());
                    log.info("간식 상세 사진 파일 업로드 완료: {}", uploadPath);

                    TreatsDetailPic detailPic = new TreatsDetailPic();
                    detailPic.setTreatsDetailPic(uploadPath);
                    detailPic.setTreats(treats);
                    treats.getTreatsDetailPics().add(detailPic);

                    log.info("간식 상세 사진 추가 완료: {}", detailPic);
                } catch (Exception e) {
                    log.error("간식 상세 사진 처리 중 오류 발생: {}", e.getMessage(), e);
                }
            }
        } else {
            log.warn("간식 상세 사진 리스트가 null이거나 비어있습니다.");
        }
    }


}



