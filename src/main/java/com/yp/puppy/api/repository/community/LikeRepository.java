package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like,String> {
}
