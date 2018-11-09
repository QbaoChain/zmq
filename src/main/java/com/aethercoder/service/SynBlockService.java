package com.aethercoder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class SynBlockService implements Runnable  {
    private Logger logger = LoggerFactory.getLogger(QueueSubService.class);

    private QtumService qtumService;

    private BlockingDeque<String> blockingDeque;


    public SynBlockService(QtumService qtumService, BlockingDeque<String> blockingDeque){
        this.qtumService = qtumService;
        this.blockingDeque = blockingDeque;
    }

    @Override
    public void run() {
        try {
            QueueSubService.isThreadOver = false;
            int minBlockHeight = getMinBlockHeight();

            // 查询数据库中所有的区块高度
            List<Integer> heightList = qtumService.getHeightList(minBlockHeight);

            int maxBlockHeight = heightList.get(heightList.size() - 1);
            List<Integer> numberList = new ArrayList();
            for (int i = minBlockHeight; i < maxBlockHeight; i++){
                numberList.add(i);
            }

            numberList.removeAll(heightList);
            for (int blockHeight: numberList) {
                Thread.sleep(5000);
                blockingDeque.put(qtumService.getBlockHash(Long.valueOf(blockHeight)));

                logger.info("SynBlockService synMissingBlock, remaining block size: " + blockingDeque.size());
            }

            writeMaxBlockHeight(maxBlockHeight);
            QueueSubService.isThreadOver = true;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("SynBlockService synMissingBlock error: " + e);
        }
    }

    private Integer getMinBlockHeight() throws Exception{
        File file = new File("../MissMinBlockHeight.conf");
        if(!file.exists()){
            return 1;
        }

        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();
        String s = "";
        while ((s = bReader.readLine()) != null) {
            sb.append(s);//将读取的字符串添加换行符后累加存放在缓存中
        }
        bReader.close();

        return Integer.valueOf(sb.toString());
    }

    private void writeMaxBlockHeight(Integer maxBlockHeight) throws Exception{
        File file =new File("../MissMinBlockHeight.conf");

        //if file doesnt exists, then create it
        if(!file.exists()){
            file.createNewFile();
        }

        //true = append file
        FileWriter fileWritter = new FileWriter(file.getName(),false);
        fileWritter.write(maxBlockHeight.toString());
        fileWritter.close();
    }
}
