package com.yp.puppy.api.controller.user;

import com.yp.puppy.api.service.shop.OrderService;
import com.yp.puppy.api.service.user.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    @GetMapping("/users/count/today")
    public List<Long> getUsersCountToday() {
        return adminService.countUsersToday();
    }

    @GetMapping("/users/count/week")
    public List<Long> getUsersCountThisWeek() {
        return adminService.countUsersThisWeek();
    }

    @GetMapping("/users/count/month")
    public List<Long> getUsersCountThisMonth() {
        return adminService.countUsersThisMonth();
    }

    @GetMapping("/point/total/day")
    public ResponseEntity<?> getPointTotalDay(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        List<Long> totals = adminService.getPointTotalDay(startDate, endDate);
        return ResponseEntity.ok(totals);
    }

    // 누적 가입자 일 / 주 / 월별 조회 메서드
    @GetMapping("/users/cumulative/today")
    public List<Long> getCumulativeUsersCountToday() {
        return adminService.countCumulativeUsersToday();
    }

    @GetMapping("/users/cumulative/week")
    public List<Long> getCumulativeUsersCountThisWeek() {
        return adminService.countCumulativeUsersThisWeek();
    }

    @GetMapping("/users/cumulative/month")
    public List<Long> getCumulativeUsersCountThisMonth() {
        return adminService.countCumulativeUsersThisMonth();
    }


    // 일/ 주 /월 별 사용 포인트 조회
    @GetMapping("/expenses/daily")
    public List<Long> getDailyExpenses() {
        return adminService.calculateDailyExpenses();
    }

    @GetMapping("/expenses/weekly")
    public List<Long> getWeeklyExpenses() {
        return adminService.calculateWeeklyExpenses();
    }

    @GetMapping("/expenses/monthly")
    public List<Long> getMonthlyExpenses() {
        return adminService.calculateMonthlyExpenses();
    }


    // 예약의 일별 지출 계산
    @GetMapping("/expenses/reservations/daily")
    public ResponseEntity<List<Long>> getDailyReservationExpenses() {
        List<Long> dailyExpenses = adminService.calculateDailyReservationExpenses();
        return ResponseEntity.ok(dailyExpenses);
    }

    // 주문의 일별 지출 계산
    @GetMapping("/expenses/orders/daily")
    public ResponseEntity<List<Long>> getDailyOrderExpenses() {
        List<Long> dailyExpenses = adminService.calculateDailyOrderExpenses();
        return ResponseEntity.ok(dailyExpenses);
    }

    // 예약의 주별 지출 계산
    @GetMapping("/expenses/reservations/weekly")
    public ResponseEntity<List<Long>> getWeeklyReservationExpenses() {
        List<Long> weeklyExpenses = adminService.calculateWeeklyReservationExpenses();
        return ResponseEntity.ok(weeklyExpenses);
    }

    // 주문의 주별 지출 계산
    @GetMapping("/expenses/orders/weekly")
    public ResponseEntity<List<Long>> getWeeklyOrderExpenses() {
        List<Long> weeklyExpenses = adminService.calculateWeeklyOrderExpenses();
        return ResponseEntity.ok(weeklyExpenses);
    }

    // 예약의 월별 지출 계산
    @GetMapping("/expenses/reservations/monthly")
    public ResponseEntity<List<Long>> getMonthlyReservationExpenses() {
        List<Long> monthlyExpenses = adminService.calculateMonthlyReservationExpenses();
        return ResponseEntity.ok(monthlyExpenses);
    }

    // 주문의 월별 지출 계산
    @GetMapping("/expenses/orders/monthly")
    public ResponseEntity<List<Long>> getMonthlyOrderExpenses() {
        List<Long> monthlyExpenses = adminService.calculateMonthlyOrderExpenses();
        return ResponseEntity.ok(monthlyExpenses);
    }

    // 가장 많이 주문한 상품 계산
    @GetMapping("/shop/best")
    public ResponseEntity<?> getMostSalesTreats() {
        HashMap<String, Integer> checkMostSalesTreats = adminService.mostSale();

        return ResponseEntity.ok(checkMostSalesTreats);
    }

}