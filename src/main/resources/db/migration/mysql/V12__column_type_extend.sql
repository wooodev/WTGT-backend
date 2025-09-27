ALTER TABLE `catsgotogedog`.`sights_information`
    CHANGE COLUMN `parking` `parking` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `rest_date` `rest_date` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `use_season` `use_season` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `use_time` `use_time` TEXT NULL DEFAULT NULL,
    CHANGE COLUMN `exp_age_range` `exp_age_range` TEXT NULL DEFAULT NULL;

ALTER TABLE `catsgotogedog`.`restaurant_information`
    CHANGE COLUMN `open_date` `open_date` DATE NULL DEFAULT NULL ,
    CHANGE COLUMN `open_time` `open_time` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `takeout` `takeout` VARCHAR(50) NULL DEFAULT NULL ,
    CHANGE COLUMN `reservation` `reservation` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `rest_date` `rest_date` VARCHAR(200) NULL DEFAULT NULL ;

ALTER TABLE `catsgotogedog`.`restaurant_information`
    CHANGE COLUMN `smoking` `smoking` VARCHAR(100) NULL DEFAULT NULL,
    CHANGE COLUMN `treat_menu` `treat_menu` VARCHAR(200) NULL DEFAULT NULL,
    CHANGE COLUMN `seat` `seat` VARCHAR(100) NULL DEFAULT NULL;

ALTER TABLE `catsgotogedog`.`lodge_information`
    CHANGE COLUMN `pickup_service` `pickup_service` VARCHAR(100) NULL DEFAULT NULL ,
    CHANGE COLUMN `reservation_url` `reservation_url` VARCHAR(300) NULL DEFAULT NULL,
    CHANGE COLUMN `room_type` `room_type` VARCHAR(100) NULL DEFAULT NULL,
    CHANGE COLUMN `scale` `scale` TEXT NULL DEFAULT NULL ,
    CHANGE COLUMN `sub_facility` `sub_facility` VARCHAR(200) NULL DEFAULT NULL,
    CHANGE COLUMN `refund_regulation` `refund_regulation` TEXT NULL DEFAULT NULL,
    CHANGE COLUMN `cooking` `cooking` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `foodplace` `foodplace` VARCHAR(200) NULL DEFAULT NULL,
    CHANGE COLUMN `check_in_time` `check_in_time` VARCHAR(100) NULL DEFAULT NULL ,
    CHANGE COLUMN `check_out_time` `check_out_time` VARCHAR(100) NULL DEFAULT NULL,
    CHANGE COLUMN `information` `information` VARCHAR(300) NULL DEFAULT NULL ,
    CHANGE COLUMN `parking` `parking` VARCHAR(200) NULL DEFAULT NULL ,
    CHANGE COLUMN `reservation_info` `reservation_info` VARCHAR(1000) NULL DEFAULT NULL;