package com.yp.puppy.api.dto.response.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailLikeStatusDto {
    //아이디 별 좋아요 상태, 누적 카운트
    private boolean boardLiked;
    private long boardLikeCount;
    private Map<Long, Boolean> replyLikes;
    private Map<Long, Long> replyLikeCounts;
    private Map<Long, Boolean> subReplyLikes;
    private Map<Long, Long> subReplyLikeCounts;
}
