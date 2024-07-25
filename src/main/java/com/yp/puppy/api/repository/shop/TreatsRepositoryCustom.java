package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Treats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TreatsRepositoryCustom {

    Page<Treats> findTreats(List<Treats.Allergic> userDogInfo, Pageable pageable, String sort);

}
