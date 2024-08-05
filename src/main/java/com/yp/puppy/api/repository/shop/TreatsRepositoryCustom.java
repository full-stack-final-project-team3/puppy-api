package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TreatsRepositoryCustom {

    Page<Treats> findTreats(List<Treats.Allergic> userDogAllergiesInfo,
                            Dog.DogSize dogSize,
                            Dog.DogAgeType dogAgeType,
                            Pageable pageable,
                            String sort);

}
