ALTER TABLE `d_qbao_chain_schema`.`t_address_info`
DROP INDEX `idx_address` ;

ALTER TABLE `d_qbao_chain_schema`.`t_address_info`
DROP INDEX `index_address` ,
ADD INDEX `index_address` (`address` ASC, `block_height` DESC);
