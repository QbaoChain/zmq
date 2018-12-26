package com.aethercoder.controller;

import com.aethercoder.dto.SearchResult;
import com.aethercoder.entity.BlockInfo;
import com.aethercoder.entity.TxInfo;
import com.aethercoder.service.QtumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hepengfei on 25/01/2018.
 */
@RestController
@RequestMapping(value = "qtumRPC", produces = "application/json")
public class QtumController {
    private static Logger logger = LoggerFactory.getLogger(QtumController.class);

    @Autowired
    private QtumService qtumService;

    @RequestMapping( value = "/blockInfos", method = RequestMethod.GET)
    public Map getLatestBlockInfos(@RequestParam("size") Integer size, @RequestParam("page") Integer page) throws Exception {
        logger.info("/blockInfos");

        return qtumService.getLatestBlockInfos(size, page);
    }

    @RequestMapping( value = "/blockInfo", method = RequestMethod.GET)
    public BlockInfo getBlockInfo(@RequestParam String blockHashOrBlockCount) throws Exception {
        logger.info("/blockInfo");

        return qtumService.getBlockInfo(blockHashOrBlockCount);
    }

    @GetMapping( value = "maxBlockHeight")
    public Map getMaxBlockHeight() {
        long maxHeight = qtumService.getMaxBlockHeight();
        Map<String, Long> result = new HashMap<>();
        result.put("maxHeight", maxHeight);
        return result;
    }

    @RequestMapping( value = "/addressInfo", method = RequestMethod.GET)
    public Map getAddressInfo(@RequestParam String address) throws Exception {
        logger.info("/addressInfo");

        return qtumService.getAddressInfos(address);
    }

    @GetMapping( value = "/addressTxList")
    public List getAddressList(@RequestParam String address, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "page", required = false) Integer page) throws Exception {
        logger.info("/addressTxList");
        return qtumService.getTxListByAddress(address, size, page);
    }

    @RequestMapping( value = "/transactionInfo", method = RequestMethod.GET)
    public Page<TxInfo> getTransactionInfo(@RequestParam(value = "txHash", required = false) String txHash, @RequestParam(value = "blockHeight", required = false) Integer blockHeight,
                                           @RequestParam(required = false) String blockHash,
                                           @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "page", required = false) Integer page) throws Exception {
        logger.info("/transactionInfo");

        return qtumService.getTransactionInfo(page, size, txHash, blockHeight, blockHash);
    }

    @RequestMapping( value = "/tokenBalance", method = RequestMethod.GET)
    public Map getTokenBalance(@RequestParam String address) throws Exception {
        logger.info("/tokenBalance");

        return qtumService.getTokenBalance(address);
    }

    @RequestMapping( value = "/queryByParam", method = RequestMethod.GET)
    public SearchResult queryByParam(@RequestParam String param) throws Exception {
        logger.info("/queryByParam");

        return qtumService.queryByParam(param);
    }
}
