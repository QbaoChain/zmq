package com.aethercoder.websocket;

import com.aethercoder.service.QtumService;
import com.aethercoder.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by hepengfei on 27/02/2018.
 */
@Component
public class MessageSender {
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private WebSocketConnectEventListener webSocketConnectEventListener;

    @Autowired
    private QtumService qtumService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String qName = "/qbaoChain/response";

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }


    public void sendEventToClient(String result) {
        logger.info("run send web socket11111");
        simpMessageSendingOperations.convertAndSend(qName , result);

    }

//    @Scheduled(fixedRate = 5000)
    public void sendLatestBlockAndTx(Integer height) {
        logger.info("run send web socket");
        Map blockTxMap = qtumService.getBlockAndTx(height);
        sendEventToClient(BeanUtils.objectToJson(blockTxMap));
    }
}