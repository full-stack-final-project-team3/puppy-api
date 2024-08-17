package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.Like;
import com.yp.puppy.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {  // Long으로 변경
    Like findByTypeAndTypeIdAndUser(String type, Long typeId, User user);
    boolean existsByTypeAndTypeIdAndUser(String type, Long typeId, User user);

    @Query("SELECT l.typeId FROM Like l WHERE l.type = :type AND l.user = :user")
    List<Long> findLikedTypeIdsByTypeAndUser(@Param("type") String type, @Param("user") User user);


    long countByTypeAndTypeId(String type, Long typeId);

    @Query("SELECT l.typeId, COUNT(l) FROM Like l WHERE l.type = :type AND l.typeId IN :typeIds GROUP BY l.typeId")
    List<Object[]> countLikesByTypeAndTypeIds(@Param("type") String type, @Param("typeIds") List<Long> typeIds);
}

