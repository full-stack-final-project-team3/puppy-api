package com.yp.puppy.api.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.user.Dog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yp.puppy.api.entity.user.QDog.dog;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DogRepositoryCustomImpl implements DogRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public void findDogByUserId(String userId, String dogId) {
        List<Dog> dogList = factory
                .selectFrom(dog)
                .where(dog.user.id.eq(userId))
                .fetch();
        Dog foundDog = factory
                .selectFrom(dog)
                .where(dog.id.eq(dogId))
                .fetchOne();
        return;
    }


}
