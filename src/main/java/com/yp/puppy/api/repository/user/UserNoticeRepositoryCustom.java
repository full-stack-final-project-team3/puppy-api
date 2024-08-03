package com.yp.puppy.api.repository.user;

import com.yp.puppy.api.entity.user.UserNotice;

import java.util.List;

public interface UserNoticeRepositoryCustom {

    List<UserNotice> findByTimeDesc(String userId);
}
