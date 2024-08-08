package com.yp.puppy.api.service.user;

import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;


    public long countUsersToday() {
        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return userRepository.countUsersByPeriod(startOfDay, endOfDay);
    }

    public long countUsersThisWeek() {
        LocalDateTime startOfWeek = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        return userRepository.countUsersByPeriod(startOfWeek, endOfWeek);
    }

    public long countUsersThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return userRepository.countUsersByPeriod(startOfMonth, endOfMonth);
    }
}

