package com.aethercoder.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by king on 11/09/2018.
 */
@Entity
@Table(name = "t_tx_info")
public class TxInfo extends BaseEntity {
    @Column(name = "tx_id")
    private String txId;

    @Column(name = "time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "block_height")
    private Integer blockHeight;

    @Column(name = "size")
    private Integer size;

    @Column(name = "tx_fee")
    private BigDecimal txFee;

    @Column(name = "tx_vincount")
    private Integer txVincount;

    @Column(name = "tx_vin")
    @Type(type="text")
    private String txVin;

    @Column(name = "tx_voutcount")
    private Integer txVoutcout;

    @Column(name = "tx_vout")
    @Type(type="text")
    private String txVout;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public BigDecimal getTxFee() {
        return txFee;
    }

    public void setTxFee(BigDecimal txFee) {
        this.txFee = txFee;
    }

    public String getTxVin() {
        return txVin;
    }

    public void setTxVin(String txVin) {
        this.txVin = txVin;
    }

    public String getTxVout() {
        return txVout;
    }

    public void setTxVout(String txVout) {
        this.txVout = txVout;
    }

    public Integer getTxVincount() {
        return txVincount;
    }

    public void setTxVincount(Integer txVincount) {
        this.txVincount = txVincount;
    }

    public Integer getTxVoutcout() {
        return txVoutcout;
    }

    public void setTxVoutcout(Integer txVoutcout) {
        this.txVoutcout = txVoutcout;
    }
}
