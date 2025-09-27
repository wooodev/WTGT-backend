ALTER TABLE `catsgotogedog`.`content`
DROP FOREIGN KEY `region_code_region_id`;

ALTER TABLE `catsgotogedog`.`category_code`
    ADD COLUMN `content_type_id` INT NULL AFTER `category_name`;

ALTER TABLE `catsgotogedog`.`content`
DROP COLUMN `region_id`,
ADD COLUMN `contentcol` VARCHAR(45) NULL AFTER `content_type_id`,
ADD COLUMN `sido_code` INT NULL AFTER `contentcol`,
ADD COLUMN `sigungu_code` INT NULL AFTER `sido_code`,
DROP INDEX `region_code_region_id_idx`;

ALTER TABLE `catsgotogedog`.`region_code`
    ADD COLUMN `sido_code` INT NULL AFTER `region_name`,
ADD COLUMN `sigungu_code` INT NULL AFTER `sido_code`,
ADD UNIQUE INDEX `sido_sigungu_code_UNIQUE` (`sido_code` ASC, `sigungu_code` ASC) VISIBLE;

ALTER TABLE `catsgotogedog`.`region_code`
    ADD COLUMN `region_level` INT NULL AFTER `parent_code`;