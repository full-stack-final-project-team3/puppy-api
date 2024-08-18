package com.yp.puppy.api.controller.user;

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
import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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
}