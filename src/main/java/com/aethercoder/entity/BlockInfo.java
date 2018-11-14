package com.aethercoder.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by king on 11/09/2018.
 */
@Entity
@Table(name = "t_block_info")
public class BlockInfo extends BaseEntity{
    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "block_height")
    private Integer blockHeight;

    @Column(name = "block_size")
    private Integer blockSize;

    @Column(name = "block_weight")
    private Integer blockWeight;

    @Column(name = "block_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date blockTime;

    @Column(name = "block_award")
    private BigDecimal blockAward;

    @Column(name = "block_merkle")
    private String blockMerkle;

    @Column(name = "block_miner")
    private String blockMiner;

    @Column(name = "block_txcount")
    private Integer blockTxcount;

    @Column(name = "block_preblockhash")
    private String blockPreblockhash;

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public Integer getBlockWeight() {
        return blockWeight;
    }

    public void setBlockWeight(Integer blockWeight) {
        this.blockWeight = blockWeight;
    }

    public Date getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Date blockTime) {
        this.blockTime = blockTime;
    }

    public BigDecimal getBlockAward() {
        return blockAward;
    }

    public void setBlockAward(BigDecimal blockAward) {
        this.blockAward = blockAward;
    }

    public String getBlockMerkle() {
        return blockMerkle;
    }

    public void setBlockMerkle(String blockMerkle) {
        this.blockMerkle = blockMerkle;
    }

    public String getBlockMiner() {
        return blockMiner;
    }

    public void setBlockMiner(String blockMiner) {
        this.blockMiner = blockMiner;
    }

    public Integer getBlockTxcount() {
        return blockTxcount;
    }

    public void setBlockTxcount(Integer blockTxcount) {
        this.blockTxcount = blockTxcount;
    }

    public String getBlockPreblockhash() {
        return blockPreblockhash;
    }

    public void setBlockPreblockhash(String blockPreblockhash) {
        this.blockPreblockhash = blockPreblockhash;
    }
}
