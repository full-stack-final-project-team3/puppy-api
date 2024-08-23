package com.yp.puppy.api.service.user;

import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.repository.hotel.ReservationRepository;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.shop.OrderRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final BundleRepository bundleRepository;


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

    public List<Long> calculateDailyExpenses() {
        List<Long> dailyExpenses = new ArrayList<>();
        for (int i = 27; i >= 0; i--) {
            LocalDateTime startOfDay = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            Long reservationTotal = reservationRepository.sumTotalPriceByPeriod(startOfDay, endOfDay);
            Long orderTotal = orderRepository.sumTotalPriceByPeriod(startOfDay, endOfDay);

            Long dailyTotal = (reservationTotal != null ? reservationTotal : 0L) +
                    (orderTotal != null ? orderTotal : 0L);

            dailyExpenses.add(dailyTotal);
        }
        return dailyExpenses;
    }

    public List<Long> calculateWeeklyExpenses() {
        List<Long> weeklyExpenses = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            LocalDateTime startOfWeek = LocalDate.now().minusWeeks(i).with(java.time.DayOfWeek.MONDAY).atStartOfDay();
            LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);

            Long reservationTotal = reservationRepository.sumTotalPriceByPeriod(startOfWeek, endOfWeek);
            Long orderTotal = orderRepository.sumTotalPriceByPeriod(startOfWeek, endOfWeek);

            Long weeklyTotal = (reservationTotal != null ? reservationTotal : 0L) +
                    (orderTotal != null ? orderTotal : 0L);

            weeklyExpenses.add(weeklyTotal);
        }
        return weeklyExpenses;
    }

    public List<Long> calculateMonthlyExpenses() {
        List<Long> monthlyExpenses = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime startOfMonth = LocalDate.now().minusMonths(i).withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

            Long reservationTotal = reservationRepository.sumTotalPriceByPeriod(startOfMonth, endOfMonth);
            Long orderTotal = orderRepository.sumTotalPriceByPeriod(startOfMonth, endOfMonth);

            Long monthlyTotal = (reservationTotal != null ? reservationTotal : 0L) +
                    (orderTotal != null ? orderTotal : 0L);

            monthlyExpenses.add(monthlyTotal);
        }
        return monthlyExpenses;
    }

    // 예약의 일별 지출 계산
    public List<Long> calculateDailyReservationExpenses() {
        List<Long> dailyExpenses = new ArrayList<>();
        for (int i = 27; i >= 0; i--) {
            LocalDateTime startOfDay = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            Long reservationTotal = reservationRepository.sumTotalPriceByPeriod(startOfDay, endOfDay);
            dailyExpenses.add(reservationTotal != null ? reservationTotal : 0L);
        }
        return dailyExpenses;
    }

    // 주문의 일별 지출 계산
    public List<Long> calculateDailyOrderExpenses() {
        List<Long> dailyExpenses = new ArrayList<>();
        for (int i = 27; i >= 0; i--) {
            LocalDateTime startOfDay = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            Long orderTotal = orderRepository.sumTotalPriceByPeriod(startOfDay, endOfDay);
            dailyExpenses.add(orderTotal != null ? orderTotal : 0L);
        }
        return dailyExpenses;
    }

    // 예약의 주별 지출 계산
    public List<Long> calculateWeeklyReservationExpenses() {
        List<Long> weeklyExpenses = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            LocalDateTime startOfWeek = LocalDate.now().minusWeeks(i).with(java.time.DayOfWeek.MONDAY).atStartOfDay();
            LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);

            Long reservationTotal = reservationRepository.sumTotalPriceByPeriod(startOfWeek, endOfWeek);
            weeklyExpenses.add(reservationTotal != null ? reservationTotal : 0L);
        }
        return weeklyExpenses;
    }

    // 주문의 주별 지출 계산
    public List<Long> calculateWeeklyOrderExpenses() {
        List<Long> weeklyExpenses = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            LocalDateTime startOfWeek = LocalDate.now().minusWeeks(i).with(java.time.DayOfWeek.MONDAY).atStartOfDay();
            LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);

            Long orderTotal = orderRepository.sumTotalPriceByPeriod(startOfWeek, endOfWeek);
            weeklyExpenses.add(orderTotal != null ? orderTotal : 0L);
        }
        return weeklyExpenses;
    }

    // 예약의 월별 지출 계산
    public List<Long> calculateMonthlyReservationExpenses() {
        List<Long> monthlyExpenses = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime startOfMonth = LocalDate.now().minusMonths(i).withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

            Long reservationTotal = reservationRepository.sumTotalPriceByPeriod(startOfMonth, endOfMonth);
            monthlyExpenses.add(reservationTotal != null ? reservationTotal : 0L);
        }
        return monthlyExpenses;
    }

    // 주문의 월별 지출 계산
    public List<Long> calculateMonthlyOrderExpenses() {
        List<Long> monthlyExpenses = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime startOfMonth = LocalDate.now().minusMonths(i).withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

            Long orderTotal = orderRepository.sumTotalPriceByPeriod(startOfMonth, endOfMonth);
            monthlyExpenses.add(orderTotal != null ? orderTotal : 0L);
        }
        return monthlyExpenses;
    }

    public HashMap<String, Integer> mostSale() {
        List<Bundle> all = bundleRepository.findAll();
        HashMap<String, Integer> treatSales = new HashMap<>();

        for (Bundle bundle : all) {
            if(bundle.getBundleStatus() == Bundle.BundleStatus.ORDERED) {
                List<Treats> treats = bundle.getTreats();
                for (Treats treat : treats) {
                    String treatName = treat.getTreatsTitle();
                    treatSales.put(treatName, treatSales.getOrDefault(treatName, 0) + 1);
                }
            }
        }

        return treatSales;
    }


}