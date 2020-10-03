package ru.zakrzhevskiy.lighthouse.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zakrzhevskiy.lighthouse.service.ClearCatalogService;


@Component
@EnableScheduling
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ClearCatalogService service;

    @Scheduled(cron = "${temp.catalog.clear.cron}")
    public void clearTmpCatalog() {
        logger.info("Temp catalog clear started.");
        service.clearCatalogRecursively();
    }

}
