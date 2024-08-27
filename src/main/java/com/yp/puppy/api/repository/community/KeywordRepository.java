package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    boolean existsByName(String name);
}