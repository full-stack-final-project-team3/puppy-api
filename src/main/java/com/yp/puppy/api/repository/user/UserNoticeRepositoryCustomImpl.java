package com.yp.puppy.api.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.user.QUserNotice;
import com.yp.puppy.api.entity.user.UserNotice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserNoticeRepositoryCustomImpl implements UserNoticeRepositoryCustom{

    private final JPAQueryFactory factory;

    @Override
    public List<UserNotice> findByTimeDesc(String userId) {
        QUserNotice userNotice = QUserNotice.userNotice;

        return factory
                .selectFrom(userNotice)
                .where(userNotice.user.id.eq(userId))
                .orderBy(userNotice.createdAt.desc())
                .fetch();
    }
}
