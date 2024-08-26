package com.yp.puppy.api.service.community;

import com.yp.puppy.api.entity.community.Keyword;
import com.yp.puppy.api.repository.community.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Transactional
    public Keyword registerKeyword(String name) {
        if (keywordRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 키워드입니다.");
        }

        Keyword keyword = Keyword.builder()
                .name(name)
                .build();

        return keywordRepository.save(keyword);
    }
    //--
    public List<Keyword> getAllKeywords() {
        return keywordRepository.findAll();
    }

    @Transactional
    public void deleteKeyword(Long id, boolean isAdmin) {

        // 사용자 권한 체크
        if (!isAdmin) {
            throw new IllegalStateException("You don't have permission to delete this board");
        }

        keywordRepository.deleteById(id);
    }
}