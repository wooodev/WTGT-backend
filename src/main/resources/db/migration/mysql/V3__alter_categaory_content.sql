ALTER TABLE `catsgotogedog`.`content`
DROP FOREIGN KEY `category_code_category_id`;

ALTER TABLE `catsgotogedog`.`category_code`
    CHANGE COLUMN `category_id` `category_id` VARCHAR(30) NOT NULL ;

ALTER TABLE `catsgotogedog`.`content`
    CHANGE COLUMN `category_id` `category_id` VARCHAR(30) NOT NULL ;

ALTER TABLE `catsgotogedog`.`content`
    ADD CONSTRAINT `category_code_category_id`
        FOREIGN KEY (`category_id`)
            REFERENCES `catsgotogedog`.`category_code` (`category_id`);

ALTER TABLE `catsgotogedog`.`content`
    CHANGE COLUMN `mapx` `mapx` DECIMAL(13,10) NULL DEFAULT NULL ,
    CHANGE COLUMN `mapy` `mapy` DECIMAL(13,10) NULL DEFAULT NULL ;

ALTER TABLE `catsgotogedog`.`content`
    ADD COLUMN `overview` TEXT NULL;
