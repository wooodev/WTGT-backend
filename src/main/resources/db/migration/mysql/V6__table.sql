ALTER TABLE `catsgotogedog`.`recur_information_room`
    CHANGE COLUMN `room_base_couint` `room_base_count` INT NULL DEFAULT NULL ;
ALTER TABLE `catsgotogedog`.`festival_information`
    ADD COLUMN `supervisor_tel` VARCHAR(50) NULL AFTER `supervisor`;
ALTER TABLE `catsgotogedog`.`lodge_information`
DROP COLUMN `lodge_informationcol`,
ADD COLUMN `goodstay` TINYINT NULL AFTER `capacity_count`;
ALTER TABLE `catsgotogedog`.`content`
    CHANGE COLUMN `tel` `tel` VARCHAR(50) NULL DEFAULT NULL ;
ALTER TABLE `catsgotogedog`.`pet_guide`
    CHANGE COLUMN `available_facility` `available_facility` VARCHAR(250) NULL DEFAULT NULL ;
ALTER TABLE `catsgotogedog`.`restaurant_information`
    CHANGE COLUMN `open_date` `open_date` VARCHAR(100) NULL DEFAULT NULL ;
