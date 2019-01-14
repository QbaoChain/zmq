package com.aethercoder.service;

import com.aethercoder.entity.AddressInfo;
import com.aethercoder.entity.BlockInfo;
import com.aethercoder.entity.TokenInfo;
import com.aethercoder.entity.TxInfo;
import com.aethercoder.util.BeanUtils;
import com.aethercoder.vo.TokenTransfer;
import com.aethercoder.websocket.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static com.aethercoder.constants.Constants.QBE_SYMBOL;

public class BlockDataService implements Runnable {

    private Logger logger = LoggerFactory.getLogger(QueueSubService.class);

    private TransactionService transactionService;

    private QtumService qtumService;

    private String blockHash;

    private Map blockDetail;

    private MessageSender messageSender;

    public BlockDataService(QtumService qtumService, TransactionService transactionService, MessageSender messageSender, String blockHash, Map blockDetail){
        this.qtumService = qtumService;
        this.transactionService = transactionService;
        this.blockHash = blockHash;
        this.blockDetail = blockDetail;
        this.messageSender = messageSender;
    }

    /**
     * 对每个区块进行数据分解
     */
    @Override
    public void run(){
        logger.info("BlockDataService current theadNumber: " + Thread.currentThread().getName());
        try{
            logger.info("BlockDataService current blockHash: " + blockHash);

            List<AddressInfo> addressInfoList = new ArrayList<AddressInfo>();
            List<TxInfo> txList = new ArrayList<TxInfo>();
            List<String> transList = (List<String>) blockDetail.get("tx");
            Map<String, BigDecimal> txFeeMap = new HashMap<>();

            // 循环每笔交易，分析每笔交易的输入和输出，记录地址余额变动情况
            for (String txHash : transList) {

                generateAddressAndTxInfo(txHash,
                        addressInfoList,
                        txList,
                        txFeeMap);
            }

            // 生成区块信息（包括区块奖励，区块交易数等）
            BlockInfo block = generateBlockInfo(txFeeMap.get("blockFee"), blockHash, transList.size());
            transactionService.save(block, txList, addressInfoList);
            //如果当前时间和区块生成的时间间隔小于10s
            if (System.currentTimeMillis() - block.getBlockTime().getTime() <= 10000) {
                messageSender.sendLatestBlockAndTx(block.getBlockHeight());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("BlockDataService current blockHash: " + blockHash + " " + e.getMessage(), e);
        }
    }

    private void generateAddressAndTxInfo(String txid,
                                         List<AddressInfo> addressInfoList,
                                         List<TxInfo> txInfos,
                                         Map<String, BigDecimal> txFeeMap) throws Exception{
        Map transDetail = qtumService.getTransDetail(txid);
        List<Map> vin = (List)transDetail.get("vin");

        //地址余额变动的集合
        Map<String, AddressInfo> addressValueMap = new HashMap<String, AddressInfo>();
        List<Map> txVin = new ArrayList<>();
        List<Map> txVout = new ArrayList<>();
        boolean isBaseFlag = false;
        BigDecimal txFee = new BigDecimal(0);

        // 处理交易输入（挖矿交易、合约输入）
        for (Map map: vin){
            if (map.get("coinbase") != null){
                Map txVinMap = new HashMap();
                txVinMap.put("address", "Coinbase");
                txVinMap.put("value", 0);
                txVin.add(txVinMap);
                isBaseFlag = true;
                continue;
            }

            List<Map> vinTxMap = (List)qtumService.getTransDetail((String) map.get("txid")).get("vout");
            Map scriptPubKeyMap = null;
            for (Map vinTxMapPer : vinTxMap) {
                if (((Integer)map.get("vout")) == (Integer) vinTxMapPer.get("n")){
                    String address = "";
                    scriptPubKeyMap = (Map)vinTxMapPer.get("scriptPubKey");

                    //如果是合约调用 (合约调用 和 普通转账)
                    if("call".equalsIgnoreCase((String)scriptPubKeyMap.get("type"))){
                        address = ((String) scriptPubKeyMap.get("asm")).split(" ")[4];
                    }
                    else {
                        address = (String) ((List)scriptPubKeyMap.get("addresses")).get(0);
                    }

                    txFee = txFee.add(new BigDecimal(vinTxMapPer.get("value").toString()));
                    if (addressValueMap.containsKey(address)){
                        AddressInfo addressInfo = addressValueMap.get(address);
                        addressInfo.setBalance_change(addressInfo.getBalance_change().subtract(new BigDecimal(vinTxMapPer.get("value").toString())));
                    }
                    else{
                        //生成输入地址的信息
                        AddressInfo addressInfo = generateAddressInfo(addressInfoList,
                                address,
                                new BigDecimal(vinTxMapPer.get("value").toString()).negate(),
                                txid,
                                (Integer) transDetail.get("time"),
                                QBE_SYMBOL,
                                null);

                        addressValueMap.put(address, addressInfo);
                    }

                    Map txVinMap = new HashMap();
                    txVinMap.put("address", address);
                    txVinMap.put("value", vinTxMapPer.get("value"));
                    txVin.add(txVinMap);
                }
            }
        }

        // 处理交易输出
        txFee = handleVoutMap(txFee, transDetail, txVout, isBaseFlag, addressInfoList, txFeeMap, addressValueMap);

        txFee = isBaseFlag ? txFeeMap.get("blockFee") : txFee;

        // 生成transaction信息
        generateTxInfo(transDetail, txInfos, txFee, BeanUtils.objectToJson(txVin), BeanUtils.objectToJson(txVout), vin.size(), ((List)transDetail.get("vout")).size());
    }

    private BigDecimal handleVoutMap(BigDecimal txFee,
                                     Map transDetail,
                                     List<Map> txVout,
                                     boolean isBaseFlag,
                                     List<AddressInfo> addressInfoList,
                                     Map<String, BigDecimal> txFeeMap,
                                     Map<String, AddressInfo> addressValueMap) throws Exception{
        List<Map> vout = (List)transDetail.get("vout");

        String address = "";
        for (Map map: vout){
            Map scriptPubKey = (Map) map.get("scriptPubKey");

            //如果是合约调用 (合约调用 和 普通转账、挖矿)
            if("call".equalsIgnoreCase((String)scriptPubKey.get("type"))){
                String[] asmBody = ((String) scriptPubKey.get("asm")).split(" ");
                if (asmBody.length > 3) {
                    address = asmBody[4];

                    if (asmBody[3].startsWith("a9059cbb")){
                        List<String> contractAddressList = new ArrayList<>();
                        contractAddressList.add(address);
                        List eventLogList = qtumService.getEventLog(((Integer)blockDetail.get("height")).longValue(), ((Integer)blockDetail.get("height")).longValue(), contractAddressList);
                        if (eventLogList.size() > 0)
                        {
                            Map<String, TokenInfo> tokenInfoMap = qtumService.getTokenIndoMap();
                            if (tokenInfoMap.containsKey(address)){
                                TokenTransfer tokenTransfer = new TokenTransfer(asmBody[3]);

                                BigDecimal value = new BigDecimal(tokenTransfer.getValue()).divide(new BigDecimal(Math.pow(10, tokenInfoMap.get(address).getDecimal())));

                                // 生成代币交易的VoutMap
                                setVinAndVout(address, value, tokenInfoMap.get(address).getSymbol(), scriptPubKey, txVout);

                                //如果输出地址不在输入列表里面，则为新的地址生成余额变动信息
                                generateAddressInfo(addressInfoList,
                                        tokenTransfer.getToAddress(),
                                        value,
                                        (String) transDetail.get("txid"),
                                        (Integer)transDetail.get("time"),
                                        tokenInfoMap.get(address).getSymbol(),
                                        address);
                            }
                        }
                    }
                }
            }
            else {
                address = scriptPubKey.containsKey("addresses") ? (String) ((List)(scriptPubKey.get("addresses"))).get(0) : "nulldata";
            }

            // 如果是挖矿交易，则输出序号为0的value则为区块奖励
            if(isBaseFlag && 0 == (Integer)map.get("n")){
                txFeeMap.put("blockFee", new BigDecimal(map.get("value").toString()));
            }
            else if(isBaseFlag && 0 != (Integer)map.get("n")){
                txFeeMap.put(address, new BigDecimal(map.get("value").toString()));
            }

            //如果输出地址不在输入列表里面，则为新的地址生成余额变动信息
            if (addressValueMap.containsKey(address)){
                AddressInfo addressInfo = addressValueMap.get(address);
                addressInfo.setBalance_change(addressInfo.getBalance_change().add(new BigDecimal(map.get("value").toString())));
            }
            else{
                generateAddressInfo(addressInfoList,
                        address,
                        new BigDecimal(map.get("value").toString()),
                        (String) transDetail.get("txid"),
                        (Integer)transDetail.get("time"),
                        QBE_SYMBOL,
                        null);
            }

            // 生成交易的VoutMap
            setVinAndVout(address, new BigDecimal(map.get("value").toString()), QBE_SYMBOL, scriptPubKey, txVout);

            // 计算交易的手续费时 扣除 花费返回部分
            BigDecimal addressFee = txFeeMap.get(address) == null ? new BigDecimal(0) : txFeeMap.get(address);
            txFee = txFee.subtract(new BigDecimal(map.get("value").toString()).add(addressFee));
//            txFee -= (double)map.get("value") + (txFeeMap.get(address) == null ? 0 : txFeeMap.get(address));
        }

        return txFee;
    }

    private void setVinAndVout(String address, BigDecimal value, String symbol, Map scriptPubKey, List<Map> vinOrVout){
        Map txVinOrVoutMap = new HashMap();
        txVinOrVoutMap.put("address", address);
        txVinOrVoutMap.put("value", value);
        txVinOrVoutMap.put("symbol", symbol);
        txVinOrVoutMap.put("Type", scriptPubKey.get("type"));
        txVinOrVoutMap.put("asm", scriptPubKey.get("asm"));
        vinOrVout.add(txVinOrVoutMap);
    }

    private void generateTxInfo(Map transDetail, List<TxInfo> txInfos, BigDecimal txFee, String txVin, String txVout, Integer vin, Integer vout){
        TxInfo txInfo = new TxInfo();
        txInfo.setBlockHash((String)transDetail.get("blockhash"));
        txInfo.setSize((Integer) transDetail.get("size"));
        txInfo.setTime(new Date(new Long((Integer)transDetail.get("time")) * 1000));
        txInfo.setTxId((String)transDetail.get("txid"));
        txInfo.setTxVincount(vin);
        txInfo.setTxVin(txVin);
        txInfo.setTxVoutcout(vout);
        txInfo.setTxVout(txVout);
        txInfo.setTxFee(txFee);
        txInfo.setBlockHeight((Integer) blockDetail.get("height"));
        txInfos.add(txInfo);
    }

    private AddressInfo generateAddressInfo(List<AddressInfo> addressInfoList,
                                           String addrress,
                                           BigDecimal change,
                                           String txid,
                                           Integer time,
                                           String symbol,
                                           String contractAddress){
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setAddress(addrress);
        addressInfo.setBalance_change(change.setScale(8, BigDecimal.ROUND_HALF_UP));
        addressInfo.setTx_hash(txid);
        addressInfo.setBlockHeight((Integer) blockDetail.get("height"));
        addressInfo.setTime(new Date(new Long(time) * 1000));
        addressInfo.setTokenSymbol(symbol);
        addressInfo.setTokenAddress(contractAddress);
        addressInfoList.add(addressInfo);

        return addressInfo;
    }

    private BlockInfo generateBlockInfo(BigDecimal blockAward, String blockHash, Integer txCount){
        BlockInfo block = qtumService.blockDetailToBlock(blockDetail);
        block.setBlockAward(blockAward);
        block.setBlockHash(blockHash);
        block.setBlockMerkle((String)blockDetail.get("merkleroot"));
        block.setBlockMiner((String) blockDetail.get("miner"));
        block.setBlockSize((Integer) blockDetail.get("size"));
        block.setBlockTime(new Date(new Long((Integer)blockDetail.get("time")) * 1000));
        block.setBlockTxcount(txCount);
        block.setBlockWeight((Integer)blockDetail.get("weight"));
        block.setBlockHeight((Integer)blockDetail.get("height"));
        block.setBlockPreblockhash((String)blockDetail.get("previousblockhash"));

        return block;
    }
}
