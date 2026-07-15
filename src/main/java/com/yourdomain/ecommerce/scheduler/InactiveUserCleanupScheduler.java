package com.yourdomain.ecommerce.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InactiveUserCleanupScheduler {

    @Scheduled(cron = "${app.scheduler.inactive-user-cleanup-cron:0 0 3 * * *}", zone = "UTC")
    public void cleanupInactiveUsers() {
        log.info("Running inactive-user cleanup job");
        // Placeholder: archive/soft-delete users that have not logged in for N days.
    }
}
