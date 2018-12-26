package com.aethercoder.service;

import com.aethercoder.dao.AddressInfoDao;
import com.aethercoder.dao.BlockInfoDao;
import com.aethercoder.dao.TokenInfoDao;
import com.aethercoder.dao.TxInfoDao;
import com.aethercoder.dto.SearchResult;
import com.aethercoder.entity.AddressInfo;
import com.aethercoder.entity.BlockInfo;
import com.aethercoder.entity.TokenInfo;
import com.aethercoder.entity.TxInfo;
import com.aethercoder.util.BeanUtils;
import com.aethercoder.util.NetworkUtil;
import com.aethercoder.util.QtumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by hepengfei on 23/01/2018.
 */
@Service
public class QtumService {

    private Logger logger = LoggerFactory.getLogger(QtumService.class);

    @Value("${qtum.username}")
    private String username;

    @Value("${qtum.password}")
    private String password;

    @Value("${qtum.url}")
    private String qtumUrl;

    @Value("${qtum.event_url}")
    private String eventUrl;

    @Autowired
    private BlockInfoDao blockInfoDao;

    @Autowired
    private TokenInfoDao tokenInfoDao;

    @Autowired
    private TxInfoDao txInfoDao;

    @Autowired
    private AddressInfoDao addressInfoDao;

    public Integer getBlockCount() throws Exception{
        List<Map<String, Object>> paramList = new ArrayList<>();
        Integer blockCount = (Integer) callQtumService("getblockcount",paramList);
        return blockCount;
    }

    public HashMap getTransDetail(String txhash) throws Exception{
        List txidParamList = new ArrayList();
        txidParamList.add(txhash);
        txidParamList.add(true);

        HashMap rawTraction = (HashMap)callQtumService("getrawtransaction", txidParamList);

        return rawTraction;
    }

    public HashMap getBlockDetail(String blockHash) throws Exception{
        List blockHashParamList = new ArrayList();
        blockHashParamList.add(blockHash);

        HashMap blockInfo = (HashMap)callQtumService("getblock", blockHashParamList);

        return blockInfo;
    }

    public String getBlockHash(Long blockHeigth) throws Exception {
        List txidParamList = new ArrayList();
        txidParamList.add(blockHeigth);
        String blockHash = (String)callQtumService("getblockhash", txidParamList);
        return blockHash;
    }

    public List getEventLog(Long blockStart, Long blockEnd, List<String> contractAddrList) throws Exception{
        Map<String, List> addressMap = new HashMap<>();
        addressMap.put("addresses", contractAddrList);

        List<Object> paramsList = new ArrayList<>();
        paramsList.add(blockStart);
        paramsList.add(blockEnd);
        paramsList.add(addressMap);
        logger.info("start searchlogs " + paramsList);
        List result = (List)this.callQtumService("searchlogs", paramsList);
        logger.info("end searchlogs " + result);
        return result;
    }

    public HashMap getBlock(String blockHash) throws Exception {
        List txidParamList = new ArrayList();
        txidParamList.add(blockHash);

        HashMap rawTraction = (HashMap)callQtumService("getblock", txidParamList);
        return rawTraction;
    }

    public BlockInfo blockDetailToBlock(Map blockDetail){
        BlockInfo block = new BlockInfo();
        block.setBlockHeight(Integer.valueOf(blockDetail.get("height").toString()));
        block.setBlockHash((String)blockDetail.get("hash"));
        block.setBlockMerkle((String)blockDetail.get("merkleroot"));
        block.setBlockMiner((String)blockDetail.get("miner"));
        block.setBlockPreblockhash((String)blockDetail.get("previousblockhash"));
        block.setBlockSize((Integer)blockDetail.get("size"));
        block.setBlockTime(new Date(new Long((Integer)blockDetail.get("time") * 1000)));
        block.setBlockWeight((Integer)blockDetail.get("weight"));

        return block;
    }

    public Map getInfo() throws Exception{
        return (Map)callQtumService("getinfo", null);
    }

    public Double getSubsidy() throws Exception{
        List<Object> paramsList = new ArrayList<Object>();
        Double subsidyValue = Double.valueOf((Long) callQtumService("getsubsidy", paramsList)) / 100000000;

        return subsidyValue;
    }

    private Object callQtumService(String method, Object params) throws Exception{
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("method", method);
        if (params != null) {
            bodyMap.put("params", params);
        }

        Map<String, String> header = new HashMap<>();
        NetworkUtil.addAuth(username, password, header);
        String url = qtumUrl;
        if (method.equals("searchlogs")) {
            url = eventUrl;
        }
        HashMap result = NetworkUtil.callHttpReq(HttpMethod.POST, url, BeanUtils.objectToJson(bodyMap), header, HashMap.class);
        return result.get("result");
    }


    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public Map getLatestBlockInfos(Integer size, Integer page) {
        Map resultMap = new HashMap();

        Page<BlockInfo> blockInfosPage = getBlockInfosByPage(size, page);

        resultMap.put("blockInfos", blockInfosPage.getContent() == null ? new ArrayList<>() : blockInfosPage.getContent());
        resultMap.put("totalNumber", blockInfosPage.getTotalElements());

        return resultMap;
    }

    public Long getMaxBlockHeight() {
        return blockInfoDao.findMaxBlockHeight();
    }

    public Page<BlockInfo> getBlockInfosByPage(Integer size, Integer page){
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "block_height"));
        Page<BlockInfo> blockInfosPage = blockInfoDao.getAll(pageable);

        return blockInfosPage;
    }

    public BlockInfo getBlockInfo(String blockHashOrBlockCount) throws Exception{
        BlockInfo blockInfo = null;
        if(isInteger(blockHashOrBlockCount)){
            blockInfo = blockInfoDao.getByBlockHeight(Integer.valueOf(blockHashOrBlockCount));
        }
        else{
            blockInfo = blockInfoDao.getByBlockHash(blockHashOrBlockCount);
        }

        return blockInfo;
    }

    public Page<TxInfo> getTransactionInfo(Integer page, Integer size, String txHash, Integer blockHeight, String blockHash) throws Exception {

        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<TxInfo> txInfos = txInfoDao.findAll(new Specification<TxInfo>() {
            @Override
            public Predicate toPredicate(Root<TxInfo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != txHash && !"".equals(txHash)) {
                    list.add(criteriaBuilder.equal(root.get("txId").as(String.class), txHash));
                }
                if (null != blockHeight && !"".equals(blockHeight)) {
                    list.add(criteriaBuilder.equal(root.get("blockHeight").as(String.class), blockHeight));
                }
                if (null != blockHash && !"".equals(blockHash)) {
                    list.add(criteriaBuilder.equal(root.get("blockHash").as(String.class), blockHash));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        return txInfos;
    }

    public Map<String, TokenInfo> getTokenIndoMap(){
        List<TokenInfo> list = tokenInfoDao.getByIdNotNull();

        Map<String, TokenInfo> resultMap = new HashMap<>();
        for (TokenInfo tokenInfo: list) {
            resultMap.put(tokenInfo.getContractAddress(), tokenInfo);
        }

        return resultMap;
    }

    public List<Integer> getHeightList(Integer minBlockHeight){
        return blockInfoDao.getAllBlockHeightFromDB(minBlockHeight);
    }

    public Map getTokenBalance(String address){
        Map tokens = new HashMap();

        try{
            Map<String, TokenInfo>  tokenInfoMap = getTokenIndoMap();

            String param = QtumUtils.base58ToSha160(address);
            if (param.length() < 64) {
                String patchStr = "";
                for(int i = param.length(); i < 64; i++){
                    patchStr += "0";
                }

                param = patchStr + param;
            }

            List txidParamList = null;
            for (Map.Entry entry: tokenInfoMap.entrySet()) {
                txidParamList = new ArrayList();
                txidParamList.add(((TokenInfo)entry.getValue()).getContractAddress());
                txidParamList.add("70a08231" + param);

                HashMap map = (HashMap)callQtumService("callcontract", txidParamList);
                if(map.get("executionResult") != null){
                    BigDecimal value = new BigDecimal(Long.parseLong((String) ((Map)map.get("executionResult")).get("output"), 16));
                    if (value.compareTo(new BigDecimal(0)) <= 0){
                        continue;
                    }

                    tokens.put(((TokenInfo)entry.getValue()).getSymbol(), value.divide(new BigDecimal(Math.pow(10, ((TokenInfo)entry.getValue()).getDecimal()))));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return tokens;
    }

    public String getAddressUnSpent(String address){
        String result = "";
        List<Object> paramList = new ArrayList<>();

        try{
            List<String> addrList = new ArrayList<String>();
            addrList.add(address);
            callQtumService("importaddress", addrList);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try{
            List list = (List)callQtumService("listaddressgroupings", paramList);

            for (Object object: list) {
                List list1 = (List)object;

                for (int i = 0 ;i< list1.size() ; i++){
                    if(((List)list1.get(i)).get(0).equals(address))
                    {
                        result = ((List)list1.get(i)).get(1).toString();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public SearchResult queryByParam(String param){
        SearchResult searchResult = new SearchResult();
        if (isInteger(param)){
            BlockInfo blockInfo = blockInfoDao.getByBlockHeight(Integer.valueOf(param));
            if (blockInfo != null) {
                searchResult.setType("block");
            }
            return searchResult;
        }

        int paramLength = param.length();
        if(paramLength == 64){
            //参数长度为64为区块hash或者交易Hash
            BlockInfo blockInfo = blockInfoDao.getByBlockHash(param);
            if (blockInfo != null) {
                searchResult.setType("block");
                return searchResult;
            }
            TxInfo txInfo = txInfoDao.getByTxId(param);
            if (txInfo != null) {
                searchResult.setType("transaction");
                return searchResult;
            }
        }
        else if(paramLength == 40 || paramLength == 34){
            //参数长度为40为合约地址---参数长度为35为账户地址
            AddressInfo addressInfo = addressInfoDao.getOneAddressInfoByAddress(param);
            if (addressInfo != null) {
                searchResult.setType("address");
                return searchResult;
            }
        }

        return searchResult;
    }

    public Map getAddressInfos(String address){
        Map resultMap = new HashMap();

        resultMap.put("QBE", addressInfoDao.findSumBalanceChange(address));
        resultMap.put("tokens", getTokenBalance(address));
        resultMap.put("totalNumber", addressInfoDao.getAllTxsByAddress(address));

        return resultMap;
    }

    public List<TxInfo> getTxListByAddress(String address, Integer size, Integer page) {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "block_height"));
        Page<AddressInfo> addressInfosPage = addressInfoDao.getTxInfos(address, pageable);
        List<AddressInfo> txInfos = addressInfosPage.getContent() == null ? new ArrayList<>() : addressInfosPage.getContent();
        List<String> txIdList = txInfos.stream().map(AddressInfo::getTx_hash).collect(Collectors.toList());
        List<TxInfo> txInfoList = txInfoDao.getByTxIdInOrderByBlockHeightDesc(txIdList);
        return txInfoList;
    }

    public Map getBlockAndTx(Integer height){
        Map resultMap = new HashMap();

        // 获取最新的区块信息（10个区块）
        BlockInfo blockInfo = blockInfoDao.getByBlockHeight(height); //getBlockInfosByPage(10, 0);
//        List<BlockInfo>  blockInfos = blockInfosPage.getContent() == null ? new ArrayList<>() : blockInfosPage.getContent();

        // 获取最新的交易信息（20个区块）
//        Pageable pageable = new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "time"));
        List<TxInfo> txInfos  = txInfoDao.getByBlockHeight(height);
//        Page<TxInfo> txInfosPage = txInfoDao.getByPage(pageable);
//        List<TxInfo> txInfos = txInfosPage.getContent() == null ? new ArrayList<>() : txInfosPage.getContent();
        txInfos.forEach(txInfo -> {
            txInfo.setTxVin(null);
            txInfo.setTxVout(null);
        });

        resultMap.put("blockHeight", blockInfoDao.findMaxBlockHeight());
        resultMap.put("blocks", blockInfo);
        resultMap.put("txs", txInfos);

        return resultMap;
    }
}
