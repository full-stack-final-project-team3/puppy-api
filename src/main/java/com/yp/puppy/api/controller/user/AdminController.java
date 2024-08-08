package com.yp.puppy.api.controller.user;

import com.yp.puppy.api.service.user.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users/count/today")
    public long getUsersCountToday() {
        return adminService.countUsersToday();
    }

    @GetMapping("/users/count/week")
    public long getUsersCountThisWeek() {
        return adminService.countUsersThisWeek();
    }

    @GetMapping("/users/count/month")
    public long getUsersCountThisMonth() {
        return adminService.countUsersThisMonth();
    }


}
