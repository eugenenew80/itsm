package itdesign.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks implements ApplicationListener<ApplicationReadyEvent> {
    private boolean isReady = false;

    @Scheduled(cron = "30 */5 * * * *")
    public void startImport() {
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        isReady = true;
    }
}
