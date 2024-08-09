package com.yp.puppy.api.repository.user;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.user.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory factory;


    @Override
    public long countUsersByPeriod(LocalDateTime start, LocalDateTime end) {
        QUser user = QUser.user;
        return factory
                .selectFrom(user)
                .where(user.createdAt.between(start, end))
                .fetchCount();
    }
}
