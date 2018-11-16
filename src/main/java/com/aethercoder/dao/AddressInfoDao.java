package com.aethercoder.dao;

import com.aethercoder.entity.AddressInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by king on 11/09/2018.
 */
@Repository
public interface AddressInfoDao extends JpaRepository<AddressInfo, Long>{

    @Query(value = "select max(block_height) from t_address_info", nativeQuery = true)
    Long findMaxBlockHeight();

    @Query(value = "select DISTINCT(block_height) from t_address_info ORDER BY block_height asc", nativeQuery = true)
    List<BigInteger> getAllBlockHeightFromDB();

    @Query(value = "select sum(balance_change) from t_address_info where address = :address", nativeQuery = true)
    BigDecimal findSumBalanceChange(@Param("address") String address);
}
