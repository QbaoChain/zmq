package com.aethercoder.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.aethercoder.service.QtumService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Component;

/**
 * Created by hepengfei on 23/01/2018.
 */
@Component
public class TransactionSync {

    private static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);



//    @Scheduled(cron = "${schedule.syncTxCron}")
    public void syncUnconfirmedTx() throws Exception{
//        logger.info("开始执行scheduledBatch");
//        qtumService.syncUnconfimedTx();
//        logger.info("结束执行scheduledBatch");
    }
}
