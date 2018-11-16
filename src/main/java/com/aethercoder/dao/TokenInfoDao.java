package com.aethercoder.dao;

import com.aethercoder.entity.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by king on 20/09/2018.
 */
@Repository
public interface TokenInfoDao extends JpaRepository<TokenInfo, Long> {

    List<TokenInfo> getByIdNotNull();
}
