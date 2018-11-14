package com.aethercoder.service;

import com.aethercoder.websocket.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hepengfei on 24/01/2018.
 */
@Service
public class QueueSubService implements Runnable {
    private Logger logger = LoggerFactory.getLogger(QueueSubService.class);

    @Autowired
    private QtumService qtumService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageSender messageSender;

    @Value("${qtum.zmq}")
    private String zmqUrl;

    public static volatile boolean isThreadOver = true;

    private BlockingDeque<String> blockingDeque;

    public BlockingDeque<String> getBlockingDeque() {
        return blockingDeque;
    }

    public void setBlockingDeque(BlockingDeque<String> blockingDeque) {
        this.blockingDeque = blockingDeque;
    }

    /**
     * 执行逻辑
     */
    @Override
    public void run() {
        String blockHash = null;
        try {
            int blocks = 999;
            // 采用多线程处理每个区块，每个线程数据独立无交互
            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(20));

            while (!Thread.currentThread().isInterrupted()) {
                blockHash = blockingDeque.take();

                Map blockDetail = qtumService.getBlockDetail(blockHash);
                executor.execute(new BlockDataService(qtumService, transactionService, messageSender, blockHash, blockDetail));

                //同步丢失的区块,启动初始或者每同步处理1000个区块，启动单个线程去检查是否存在缺失的区块
                blocks++;
                if (blocks % 1000 == 0 && isThreadOver){
                    executor.execute(new SynBlockService(qtumService, blockingDeque));
                }

                logger.info("QueueSubService remaining block size: " + blockingDeque.size());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("QueueSubService current blockHash: " + blockHash + " " + e.getMessage(), e);
        }
    }
}
