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
@Table(name = "t_address_info")
public class AddressInfo extends BaseEntity {
    @Column(name = "address")
    private String address;

    @Column(name = "tx_hash")
    private String tx_hash;

    @Column(name = "balance_change")
    private BigDecimal balance_change;

    @Column(name = "time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    @Column(name = "block_height")
    private Integer blockHeight;

    @Column(name = "token_symbol")
    private String tokenSymbol;

    @Column(name = "token_address")
    private String tokenAddress;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTx_hash() {
        return tx_hash;
    }

    public void setTx_hash(String tx_hash) {
        this.tx_hash = tx_hash;
    }

    public BigDecimal getBalance_change() {
        return balance_change;
    }

    public void setBalance_change(BigDecimal balance_change) {
        this.balance_change = balance_change;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }
}
