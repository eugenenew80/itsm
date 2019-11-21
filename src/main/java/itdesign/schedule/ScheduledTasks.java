package itdesign.schedule;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.redisson.api.RedissonClient;

@Component
@RequiredArgsConstructor
public class ScheduledTasks implements ApplicationListener<ApplicationReadyEvent> {
    private static boolean isReady = false;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final RedissonClient redissonClient;

    @Scheduled(cron = "0 */1 * * * *")
    public void startImport() {
        logger.debug("GC started");
        System.gc();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        isReady = true;
    }
}
