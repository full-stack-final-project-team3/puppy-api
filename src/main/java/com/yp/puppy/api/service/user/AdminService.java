package com.yp.puppy.api.service.user;

import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<Long> countUsersToday() {
        List<Long> counts = new ArrayList<>();
        for (int i = 0; i < 28; i++) { // 지난 28일 동안의 사용자 수
            LocalDateTime startOfDay = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            long count = userRepository.countUsersByPeriod(startOfDay, endOfDay);
            counts.add(count);
        }
        return counts;
    }

    public List<Long> countUsersThisWeek() {
        List<Long> counts = new ArrayList<>();
        for (int i = 0; i < 4; i++) { // 지난 4주 동안의 사용자 수
            LocalDateTime startOfWeek = LocalDate.now().minusWeeks(i).with(java.time.DayOfWeek.MONDAY).atStartOfDay();
            LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
            long count = userRepository.countUsersByPeriod(startOfWeek, endOfWeek);
            counts.add(count);
        }
        return counts;
    }

    public List<Long> countUsersThisMonth() {
        List<Long> counts = new ArrayList<>();
        for (int i = 0; i < 12; i++) { // 지난 12개월 동안의 사용자 수
            LocalDateTime startOfMonth = LocalDate.now().minusMonths(i).withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            long count = userRepository.countUsersByPeriod(startOfMonth, endOfMonth);
            counts.add(count);
        }
        return counts;
    }
}