package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TreatsRepositoryCustom {

    Page<Treats> findTreats(List<Treats.Allergic> userDogAllergiesInfo,
                            Dog.DogSize dogSize,
                            Pageable pageable,
                            String sort);

//    // 정렬을 위한 메서드 추가
//    @Query("SELECT t FROM Treats t WHERE t.size = :dogSize AND t.allergies IN :allergics ORDER BY CASE WHEN :sort = 'asc' THEN t.name END ASC, CASE WHEN :sort = 'desc' THEN t.name END DESC")
//    Page<Treats> findTreatsBySizeAndAllergiesWithSort(@Param("dogSize") Dog.DogSize dogSize,
//                                                      @Param("allergics") List<Treats.Allergic> allergics,
//                                                      Pageable pageable,
//                                                      @Param("sort") String sort);
}
