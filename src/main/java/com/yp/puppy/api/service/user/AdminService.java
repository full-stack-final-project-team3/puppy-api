package com.yp.puppy.api.service.user;

import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
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

    public List<Long> getPointTotalDay(LocalDateTime startDate, LocalDateTime endDate) {
        List<Long> dailyTotals = new ArrayList<>();

        // 주어진 날짜 범위 동안 각 날짜를 반복
        for (LocalDateTime date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = date.toLocalDate().atTime(23, 59, 59);

            // 해당 날짜의 호텔 예약 총 비용을 가져옴
            Long hotelExpense = userRepository.sumTotalPriceHotelByDateRange(dayStart, dayEnd);
            if (hotelExpense == null) {
                hotelExpense = 0L;
            }

            // 해당 날짜의 쇼핑몰 주문 총 비용을 가져옴
            Long shopExpense = userRepository.sumTotalPriceShopByDateRange(dayStart, dayEnd);
            if (shopExpense == null) {
                shopExpense = 0L;
            }

            // 해당 날짜의 총 비용을 리스트에 추가
            dailyTotals.add(hotelExpense + shopExpense);
        }

        return dailyTotals;
    }

    // 누적 가입자 일 / 주 / 월별 조회 메서드
    public List<Long> countCumulativeUsersToday() {
        List<Long> cumulativeCounts = new ArrayList<>();
        long total = 0; // 누적 카운트

        for (int i = 27; i >= 0; i--) { // 오늘을 포함한 지난 28일 동안의 누적 사용자 수
            LocalDateTime startOfDay = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            long count = userRepository.countUsersByPeriod(startOfDay, endOfDay);
            total += count; // 누적 수에 현재 날짜의 가입자 수를 더함
            cumulativeCounts.add(total);
        }
        return cumulativeCounts;
    }

    public List<Long> countCumulativeUsersThisWeek() {
        List<Long> cumulativeCounts = new ArrayList<>();
        long total = 0; // 누적 카운트

        for (int i = 3; i >= 0; i--) { // 이번 주를 포함한 지난 4주 동안의 누적 사용자 수
            LocalDateTime startOfWeek = LocalDate.now().minusWeeks(i).with(java.time.DayOfWeek.MONDAY).atStartOfDay();
            LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
            long count = userRepository.countUsersByPeriod(startOfWeek, endOfWeek);
            total += count; // 누적 수에 현재 주의 가입자 수를 더함
            cumulativeCounts.add(total);
        }
        Collections.reverse(cumulativeCounts); // 배열의 순서를 뒤집음
        return cumulativeCounts;
    }

    public List<Long> countCumulativeUsersThisMonth() {
        List<Long> cumulativeCounts = new ArrayList<>();
        long total = 0; // 누적 카운트

        for (int i = 11; i >= 0; i--) { // 이번 달을 포함한 지난 12개월 동안의 누적 사용자 수
            LocalDateTime startOfMonth = LocalDate.now().minusMonths(i).withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            long count = userRepository.countUsersByPeriod(startOfMonth, endOfMonth);
            total += count; // 누적 수에 현재 달의 가입자 수를 더함
            cumulativeCounts.add(total);
        }
        Collections.reverse(cumulativeCounts); // 배열의 순서를 뒤집음
        return cumulativeCounts;
    }
}