ALTER TABLE `d_qbao_chain_schema`.`t_block_info`
ADD INDEX `idx_block_height` (`block_height` ASC);

ALTER TABLE `d_qbao_chain_schema`.`t_address_info`
ADD INDEX `idx_address` (`address` ASC);