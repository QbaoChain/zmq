CREATE SCHEMA `d_qbao_chain_schema` ;

CREATE TABLE `d_qbao_chain_schema`.`t_block_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `block_hash` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `block_height` int(8) DEFAULT NULL,
  `block_size` int(8) DEFAULT NULL,
  `block_weight` int(8) DEFAULT NULL,
  `block_time` datetime DEFAULT NULL,
  `block_award` decimal(16,8) DEFAULT NULL,
  `block_merkle` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `block_miner` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `block_txcount` int(8) DEFAULT NULL,
  `block_preblockhash` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_block_hash` (`block_hash`),
  KEY `index_block_height` (`block_height`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `d_qbao_chain_schema`.`t_tx_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `tx_id` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
    `tx_vincount` int(8) DEFAULT NULL,
    `tx_vin` longtext COLLATE utf8mb4_unicode_ci,
    `tx_voutcount` int(8) DEFAULT NULL,
    `tx_vout` longtext COLLATE utf8mb4_unicode_ci,
  `time` datetime DEFAULT NULL,
  `block_hash` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `block_height` int(8) DEFAULT NULL,
  `size` int(8) DEFAULT NULL,
  `tx_fee` decimal(16,8) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tx_id_UNIQUE` (`tx_id`),
  KEY `index_block_height` (`block_height`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `d_qbao_chain_schema`.`t_address_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `block_height` int(8) DEFAULT NULL,
  `tx_hash` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `balance_change` decimal(24,8) DEFAULT NULL,
    `token_symbol` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    `token_address` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_tx_hash` (`tx_hash`),
  KEY `index_address` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `d_qbao_chain_schema`.`t_token_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contract_address` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `decimal` int(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

