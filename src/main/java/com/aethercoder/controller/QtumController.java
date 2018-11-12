package com.aethercoder.controller;

import com.aethercoder.service.QtumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hepengfei on 25/01/2018.
 */
@RestController
@RequestMapping("qtumRPC")
public class QtumController {
    private static Logger logger = LoggerFactory.getLogger(QtumController.class);

    @Autowired
    private QtumService qtumService;

    @RequestMapping( value = "/blockInfos/{limit}/{offset}", method = RequestMethod.GET)
    public String getLatestBlockInfos(@PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset) throws Exception {
        logger.info("/blockInfos/{limit}/{offset}");

        return qtumService.getLatestBlockInfos(limit, offset);
    }

    @RequestMapping( value = "/blockInfo", method = RequestMethod.GET)
    public String getBlockInfo(@RequestParam String blockHashOrBlockCount) throws Exception {
        logger.info("/blockInfo");

        return qtumService.getBlockInfo(blockHashOrBlockCount);
    }

    @RequestMapping( value = "/addressInfo/{limit}/{offset}", method = RequestMethod.GET)
    public String getAddressInfo(@RequestParam String address, @PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset) throws Exception {
        logger.info("/addressInfo");

        return qtumService.getAddressInfos(address, limit, offset);
    }

    @RequestMapping( value = "/transactionInfo", method = RequestMethod.GET)
    public String getTransactionInfo(@RequestParam String txHash) throws Exception {
        logger.info("/transactionInfo");

        return qtumService.getTransactionInfo(txHash);
    }

    @RequestMapping( value = "/tokenBalance", method = RequestMethod.GET)
    public String getTokenBalance(@RequestParam String address) throws Exception {
        logger.info("/tokenBalance");

        return qtumService.getTokenBalance(address);
    }

    @RequestMapping( value = "/queryByParam", method = RequestMethod.GET)
    public String queryByParam(@RequestParam String param) throws Exception {
        logger.info("/queryByParam");

        return qtumService.queryByParam(param);
    }
}
