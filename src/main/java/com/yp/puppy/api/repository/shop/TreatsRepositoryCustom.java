package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TreatsRepositoryCustom {

    Page<Treats> findTreats(Dog userDogInfo, Pageable pageable, String sort);

}
