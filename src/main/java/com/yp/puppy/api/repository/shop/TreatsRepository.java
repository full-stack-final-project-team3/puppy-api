package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Treats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatsRepository extends JpaRepository<Treats, String>, TreatsRepositoryCustom {
}
