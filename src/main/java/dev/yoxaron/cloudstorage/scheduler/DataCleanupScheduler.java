package dev.yoxaron.cloudstorage.scheduler;

import dev.yoxaron.cloudstorage.service.DataCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataCleanupScheduler {

    private final DataCleanupService dataCleanupService;

    @Scheduled(cron = "0 * * * * *")
    public void scheduleCleanup() {
        int removeCount = dataCleanupService.cleanup(Instant.now().minus(5, ChronoUnit.MINUTES));
        log.info("Cleaned up {} objects", removeCount);
    }
}
